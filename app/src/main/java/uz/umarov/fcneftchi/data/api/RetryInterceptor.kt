package uz.umarov.fcneftchi.data.api

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import kotlin.math.pow

class RetryInterceptor(
    private val maxAttempts: Int = 3,
    private val baseDelayMs: Long = 500L
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var lastError: IOException? = null

        repeat(maxAttempts) { attempt ->
            try {
                val response = chain.proceed(request)
                if (response.isSuccessful || response.code !in RETRYABLE_STATUS) {
                    return response
                }
                response.close()
                Timber.tag("RetryInterceptor")
                    .w("Retryable status %d on attempt %d for %s", response.code, attempt + 1, request.url)
            } catch (e: IOException) {
                Timber.tag("RetryInterceptor")
                    .w(e, "IOException on attempt %d for %s", attempt + 1, request.url)
                lastError = e
            }

            if (attempt < maxAttempts - 1) {
                val delayMs = (baseDelayMs * 2.0.pow(attempt.toDouble())).toLong()
                try {
                    Thread.sleep(delayMs)
                } catch (ie: InterruptedException) {
                    Thread.currentThread().interrupt()
                    throw IOException("Retry interrupted", ie)
                }
            }
        }

        return lastError?.let { throw it } ?: chain.proceed(request)
    }

    companion object {
        private val RETRYABLE_STATUS = setOf(408, 429, 500, 502, 503, 504)
    }
}

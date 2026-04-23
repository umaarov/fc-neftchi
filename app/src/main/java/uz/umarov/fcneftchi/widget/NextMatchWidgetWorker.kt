package uz.umarov.fcneftchi.widget

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.lastOrNull
import timber.log.Timber
import uz.umarov.fcneftchi.domain.usecase.GetLastMatchUseCase
import uz.umarov.fcneftchi.domain.usecase.GetNextMatchUseCase

@HiltWorker
class NextMatchWidgetWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val getNextMatch: GetNextMatchUseCase,
    private val getLastMatch: GetLastMatchUseCase,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val next = getNextMatch().lastOrNull()
            val last = getLastMatch().lastOrNull()
            NextMatchWidgetProvider.renderWidget(applicationContext, next, last)
            Result.success()
        } catch (e: Exception) {
            Timber.tag("NextMatchWidget").w(e, "widget refresh failed")
            Result.retry()
        }
    }
}

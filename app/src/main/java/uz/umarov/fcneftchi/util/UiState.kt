package uz.umarov.fcneftchi.util

import androidx.annotation.StringRes

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(@StringRes val messageRes: Int? = null, val fallbackMessage: String? = null) :
        UiState<Nothing>()

    data object Empty : UiState<Nothing>()
}

inline fun <T, R> UiState<T>.mapSuccess(transform: (T) -> R): UiState<R> = when (this) {
    is UiState.Success -> UiState.Success(transform(data))
    is UiState.Error -> this
    UiState.Loading -> UiState.Loading
    UiState.Empty -> UiState.Empty
}

fun <T> Result<T>.toUiState(@androidx.annotation.StringRes errorFallbackRes: Int? = null): UiState<T> =
    when (this) {
        Result.Loading -> UiState.Loading
        is Result.Success -> UiState.Success(data)
        is Result.Error -> UiState.Error(
            messageRes = errorFallbackRes,
            fallbackMessage = throwable.message
        )
    }

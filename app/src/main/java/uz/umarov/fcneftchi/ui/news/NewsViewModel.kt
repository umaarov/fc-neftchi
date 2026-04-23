package uz.umarov.fcneftchi.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.domain.usecase.GetNewsFeedUseCase
import javax.inject.Inject

data class NewsUiState(
    val articles: List<NewsArticle> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsFeed: GetNewsFeedUseCase
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<NewsUiState> = retryTrigger
        .flatMapLatest {
            getNewsFeed()
                .map { articles -> NewsUiState(articles = articles, isLoading = false) }
                .onStart { emit(NewsUiState(isLoading = true)) }
                .catch { e ->
                    emit(NewsUiState(isLoading = false, error = e.message ?: "Failed to load news"))
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NewsUiState())

    fun retry() {
        retryTrigger.value += 1
    }
}

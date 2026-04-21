package uz.umarov.fcneftchi.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.domain.usecase.GetNewsFeedUseCase
import javax.inject.Inject

data class NewsUiState(
    val articles: List<NewsArticle> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class NewsViewModel @Inject constructor(
    getNewsFeed: GetNewsFeedUseCase
) : ViewModel() {

    val uiState: StateFlow<NewsUiState> = getNewsFeed()
        .map { articles -> NewsUiState(articles = articles, isLoading = false) }
        .catch { e ->
            emit(NewsUiState(isLoading = false, error = e.message ?: "Failed to load news"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NewsUiState())
}

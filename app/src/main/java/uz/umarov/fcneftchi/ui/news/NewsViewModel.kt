package uz.umarov.fcneftchi.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    private val getNewsFeed: GetNewsFeedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    init {
        loadNews()
    }

    private fun loadNews() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getNewsFeed()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            error = e.message ?: "Failed to load news",
                            isLoading = false
                        )
                    }
                }
                .collect { articles ->
                    _uiState.update {
                        it.copy(
                            articles = articles,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }
}

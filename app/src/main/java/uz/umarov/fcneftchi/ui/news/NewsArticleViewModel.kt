package uz.umarov.fcneftchi.ui.news

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.domain.usecase.GetNewsDetailUseCase
import javax.inject.Inject

data class NewsArticleState(
    val article: NewsArticle? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class NewsArticleViewModel @Inject constructor(
    private val getNewsDetail: GetNewsDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val articleUrl: String = savedStateHandle.get<String>("articleUrl")!!

    private val _uiState = MutableStateFlow(NewsArticleState())
    val uiState = _uiState.asStateFlow()

    init {
        loadArticle()
    }

    private fun loadArticle() {
        viewModelScope.launch {
            getNewsDetail(articleUrl).collect { article ->
                _uiState.update { it.copy(article = article, isLoading = false) }
            }
        }
    }
}

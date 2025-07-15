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
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

data class NewsArticleState(
    val article: NewsArticle? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class NewsArticleViewModel @Inject constructor(
    private val repository: NeftchiRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val articleId: String = savedStateHandle.get<String>("articleId")!!

    private val _uiState = MutableStateFlow(NewsArticleState())
    val uiState = _uiState.asStateFlow()

    init {
        loadArticle()
    }

    private fun loadArticle() {
        viewModelScope.launch {
            repository.getNewsArticleById(articleId).collect { article ->
                _uiState.update { it.copy(article = article, isLoading = false) }
            }
        }
    }
}
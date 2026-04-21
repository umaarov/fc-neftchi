package uz.umarov.fcneftchi.ui.news

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.domain.usecase.GetNewsDetailUseCase
import javax.inject.Inject

data class NewsArticleState(
    val article: NewsArticle? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class NewsArticleViewModel @Inject constructor(
    getNewsDetail: GetNewsDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val articleUrl: String = savedStateHandle.get<String>("articleUrl")!!

    val uiState: StateFlow<NewsArticleState> = getNewsDetail(articleUrl)
        .map { article -> NewsArticleState(article = article, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NewsArticleState())
}

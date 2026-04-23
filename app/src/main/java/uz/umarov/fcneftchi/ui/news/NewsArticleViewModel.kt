package uz.umarov.fcneftchi.ui.news

import androidx.lifecycle.SavedStateHandle
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
import uz.umarov.fcneftchi.domain.usecase.GetNewsDetailUseCase
import javax.inject.Inject

data class NewsArticleState(
    val article: NewsArticle? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NewsArticleViewModel @Inject constructor(
    private val getNewsDetail: GetNewsDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val articleUrl: String = savedStateHandle.get<String>("articleUrl")!!
    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<NewsArticleState> = retryTrigger
        .flatMapLatest {
            getNewsDetail(articleUrl)
                .map { article -> NewsArticleState(article = article, isLoading = false) }
                .onStart { emit(NewsArticleState(isLoading = true)) }
                .catch { e -> emit(NewsArticleState(isLoading = false, error = e.message)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NewsArticleState())

    fun retry() {
        retryTrigger.value += 1
    }
}

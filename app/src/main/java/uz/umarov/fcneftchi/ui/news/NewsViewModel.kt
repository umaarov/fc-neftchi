package uz.umarov.fcneftchi.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.domain.usecase.GetNewsFeedUseCase
import javax.inject.Inject

data class NewsUiState(
    val articles: List<NewsArticle> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val isFiltered: Boolean = false,
)

private data class NewsLoad(
    val articles: List<NewsArticle> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsFeed: GetNewsFeedUseCase
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)
    private val searchQuery = MutableStateFlow("")
    private val selectedCategory = MutableStateFlow<String?>(null)

    private val newsLoad: StateFlow<NewsLoad> = retryTrigger
        .flatMapLatest {
            getNewsFeed()
                .map { articles -> NewsLoad(articles = articles, isLoading = false) }
                .onStart { emit(NewsLoad(isLoading = true)) }
                .catch { e ->
                    emit(NewsLoad(isLoading = false, error = e.message ?: "Failed to load news"))
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NewsLoad())

    val uiState: StateFlow<NewsUiState> =
        combine(newsLoad, searchQuery, selectedCategory) { load, query, category ->
            val categories = load.articles
                .map { it.category }
                .filter { it.isNotBlank() }
                .distinct()
                .sorted()

            val resolvedCategory = category?.takeIf { it in categories }
            val filtered = filter(load.articles, query, resolvedCategory)
            val isFiltered = query.isNotBlank() || resolvedCategory != null

            NewsUiState(
                articles = filtered,
                categories = categories,
                selectedCategory = resolvedCategory,
                searchQuery = query,
                isLoading = load.isLoading,
                error = load.error,
                isFiltered = isFiltered,
            )
        }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NewsUiState())

    fun retry() {
        retryTrigger.value += 1
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setCategory(category: String?) {
        selectedCategory.value = category
    }

    private fun filter(
        articles: List<NewsArticle>,
        query: String,
        category: String?,
    ): List<NewsArticle> {
        val trimmed = query.trim()
        if (trimmed.isBlank() && category == null) return articles
        return articles.filter { article ->
            (category == null || article.category.equals(category, ignoreCase = true)) &&
                (trimmed.isBlank() || article.matches(trimmed))
        }
    }

    private fun NewsArticle.matches(query: String): Boolean {
        return title.contains(query, ignoreCase = true) ||
            description.contains(query, ignoreCase = true)
    }
}

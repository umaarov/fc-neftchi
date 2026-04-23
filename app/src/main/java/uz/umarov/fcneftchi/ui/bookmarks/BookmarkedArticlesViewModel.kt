package uz.umarov.fcneftchi.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.domain.usecase.ObserveBookmarkedArticlesUseCase
import javax.inject.Inject

@HiltViewModel
class BookmarkedArticlesViewModel @Inject constructor(
    observeBookmarkedArticles: ObserveBookmarkedArticlesUseCase,
) : ViewModel() {

    val articles: StateFlow<List<NewsArticle>> = observeBookmarkedArticles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

package uz.umarov.fcneftchi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.domain.model.HomeFeed
import uz.umarov.fcneftchi.domain.usecase.GetHomeFeedUseCase
import javax.inject.Inject

data class HomeUiState(
    val items: List<HomeListItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeFeed: GetHomeFeedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            if (_uiState.value.items.isEmpty()) {
                _uiState.update { it.copy(isLoading = true) }
            }

            getHomeFeed()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { feed ->
                    _uiState.value = HomeUiState(items = buildHomeItems(feed), isLoading = false)
                }
        }
    }

    private fun buildHomeItems(feed: HomeFeed): List<HomeListItem> {
        val homeItems = mutableListOf<HomeListItem>()

        if (feed.news.isNotEmpty()) {
            homeItems.add(HomeListItem.HeroCarouselItem(feed.news.take(3)))
        }

        feed.nextMatch?.let { homeItems.add(HomeListItem.NextMatchItem(it)) }
        feed.lastMatch?.let { homeItems.add(HomeListItem.LastResultItem(it)) }

        feed.videos.firstOrNull()?.let {
            homeItems.add(HomeListItem.HeaderItem("Video", R.id.videosFragment))
            homeItems.add(HomeListItem.FeaturedVideoItem(it))
        }

        val otherNews = feed.news.drop(3)
        if (otherNews.isNotEmpty()) {
            homeItems.add(HomeListItem.HeaderItem("So'nggi yangiliklar", R.id.newsFragment))
            homeItems.add(HomeListItem.NewsCarouselItem(otherNews.take(5)))
        }

        if (feed.standings.isNotEmpty()) {
            homeItems.add(HomeListItem.HeaderItem("Turnir jadvali", R.id.matchesFragment))
            homeItems.add(HomeListItem.StandingsItem(feed.standings.take(5)))
        }

        return homeItems
    }
}

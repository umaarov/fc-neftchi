package uz.umarov.fcneftchi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

data class HomeUiState(
    val items: List<HomeListItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NeftchiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            combine(
                repository.getNextMatch(),
                repository.getLastMatch(),
                repository.getNews(),
                repository.getLeagueTable(),
                repository.getVideos()
            ) { nextMatch, lastMatch, news, standings, videos ->
                val homeItems = mutableListOf<HomeListItem>()

                if (news.isNotEmpty()) {
                    homeItems.add(HomeListItem.HeroCarouselItem(news.take(3)))
                }

                nextMatch?.let { homeItems.add(HomeListItem.NextMatchItem(it)) }

                lastMatch?.let { homeItems.add(HomeListItem.LastResultItem(it)) }

                videos.firstOrNull()?.let {
                    homeItems.add(HomeListItem.HeaderItem("Video", R.id.videosFragment))
                    homeItems.add(HomeListItem.FeaturedVideoItem(it))
                }

                val otherNews = news.drop(3)
                if (otherNews.isNotEmpty()) {
                    homeItems.add(HomeListItem.HeaderItem("So'nggi yangiliklar", R.id.newsFragment))
                    homeItems.add(HomeListItem.NewsCarouselItem(otherNews.take(5)))
                }

                if (standings.isNotEmpty()) {
                    homeItems.add(HomeListItem.HeaderItem("Turnir jadvali", R.id.matchesFragment))
                    homeItems.add(HomeListItem.StandingsItem(standings.take(5)))
                }

                HomeUiState(items = homeItems, isLoading = false)

            }.catch { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }.collect { combinedState ->
                _uiState.value = combinedState
            }
        }
    }
}
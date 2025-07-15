package uz.umarov.fcneftchi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.data.model.*
import uz.umarov.fcneftchi.data.repository.NeftchiRepository
import javax.inject.Inject

data class HomeUiState(
    val nextMatch: Match? = null,
    val lastMatch: Match? = null,
    val news: List<NewsArticle> = emptyList(),
    val standings: List<LeagueStanding> = emptyList(),
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
                repository.getLeagueTable()
            ) { nextMatch, lastMatch, news, standings ->
                HomeUiState(
                    nextMatch = nextMatch,
                    lastMatch = lastMatch,
                    news = news.take(3),
                    standings = standings.take(4),
                    isLoading = false
                )
            }.catch { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }.collect { combinedState ->
                _uiState.value = combinedState
            }
        }
    }
}


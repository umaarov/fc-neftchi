package uz.umarov.fcneftchi.ui.match_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import uz.umarov.fcneftchi.data.model.GameDetail
import uz.umarov.fcneftchi.domain.usecase.GetGameDetailsUseCase
import uz.umarov.fcneftchi.util.DateUtils
import javax.inject.Inject

data class MatchDetailUiState(
    val gameDetail: GameDetail? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isLive: Boolean = false,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MatchDetailViewModel @Inject constructor(
    private val getGameDetails: GetGameDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val gameId: Int = savedStateHandle.get<Int>("gameId")!!
    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<MatchDetailUiState> = retryTrigger
        .flatMapLatest {
            flow {
                while (currentCoroutineContext().isActive) {
                    val detail = getGameDetails(gameId).first()
                    val isLive = detail?.startDate?.let(::isWithinLiveWindow) ?: false
                    emit(MatchDetailUiState(gameDetail = detail, isLoading = false, isLive = isLive))
                    if (!isLive) break
                    delay(LIVE_POLL_INTERVAL_MS)
                }
            }
                .onStart { emit(MatchDetailUiState(isLoading = true)) }
                .catch { e -> emit(MatchDetailUiState(isLoading = false, error = e.message)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MatchDetailUiState())

    fun retry() {
        retryTrigger.value += 1
    }

    private fun isWithinLiveWindow(startDate: String): Boolean {
        val kickoffMillis = DateUtils.parseDate(startDate)?.time ?: return false
        val now = System.currentTimeMillis()
        val start = kickoffMillis - PRE_KICKOFF_LEAD_MS
        val end = kickoffMillis + MATCH_DURATION_MS
        return now in start..end
    }

    private companion object {
        const val LIVE_POLL_INTERVAL_MS = 30_000L
        const val PRE_KICKOFF_LEAD_MS = 5 * 60 * 1000L
        const val MATCH_DURATION_MS = 150 * 60 * 1000L
    }
}

package uz.umarov.fcneftchi.ui.player

import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.PlayerPosition
import uz.umarov.fcneftchi.data.model.PlayerProfile
import uz.umarov.fcneftchi.domain.usecase.GetPlayerProfileUseCase
import uz.umarov.fcneftchi.domain.usecase.IsPlayerBookmarkedUseCase
import uz.umarov.fcneftchi.domain.usecase.TogglePlayerBookmarkUseCase
import javax.inject.Inject

data class PlayerProfileUiState(
    val profile: PlayerProfile? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isBookmarked: Boolean = false,
)

sealed interface PlayerProfileEvent {
    object BookmarkAdded : PlayerProfileEvent
    object BookmarkRemoved : PlayerProfileEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlayerProfileViewModel @Inject constructor(
    private val getPlayerProfile: GetPlayerProfileUseCase,
    private val isPlayerBookmarked: IsPlayerBookmarkedUseCase,
    private val togglePlayerBookmark: TogglePlayerBookmarkUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val playerId: Int = savedStateHandle.get<Int>("playerId")!!
    private val retryTrigger = MutableStateFlow(0)
    private val events = MutableStateFlow<PlayerProfileEvent?>(null)

    val uiState: StateFlow<PlayerProfileUiState> = retryTrigger
        .flatMapLatest {
            val profileFlow = getPlayerProfile(playerId)
                .map<PlayerProfile?, PlayerProfileUiState> { profile ->
                    PlayerProfileUiState(profile = profile, isLoading = false)
                }
                .onStart { emit(PlayerProfileUiState(isLoading = true)) }
                .catch { e -> emit(PlayerProfileUiState(isLoading = false, error = e.message)) }
            combine(profileFlow, isPlayerBookmarked(playerId)) { state, bookmarked ->
                state.copy(isBookmarked = bookmarked)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlayerProfileUiState())

    val event: StateFlow<PlayerProfileEvent?> = events

    fun retry() {
        retryTrigger.value += 1
    }

    fun toggleBookmark() {
        val profile = uiState.value.profile ?: return
        val wasBookmarked = uiState.value.isBookmarked
        val player = profile.toPlayer()
        viewModelScope.launch {
            togglePlayerBookmark(player)
            events.value = if (wasBookmarked) {
                PlayerProfileEvent.BookmarkRemoved
            } else {
                PlayerProfileEvent.BookmarkAdded
            }
        }
    }

    fun consumeEvent() {
        events.value = null
    }

    private fun PlayerProfile.toPlayer(): Player {
        val fullName = "${details.firstName ?: ""} ${details.lastName}".trim()
        return Player(
            id = details.id,
            name = fullName,
            number = details.number ?: 0,
            position = PlayerPosition.fromId(details.position),
            imageUrl = details.photo ?: R.drawable.player_placeholder_inset,
            nationality = details.country.title
        )
    }
}

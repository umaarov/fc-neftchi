package uz.umarov.fcneftchi.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.domain.usecase.ObserveBookmarkedPlayersUseCase
import javax.inject.Inject

@HiltViewModel
class BookmarkedPlayersViewModel @Inject constructor(
    observeBookmarkedPlayers: ObserveBookmarkedPlayersUseCase,
) : ViewModel() {

    val players: StateFlow<List<Player>> = observeBookmarkedPlayers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

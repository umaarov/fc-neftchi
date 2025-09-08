package uz.umarov.fcneftchi.ui.match_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.databinding.FragmentMatchEventsBinding
import uz.umarov.fcneftchi.ui.MainActivity
import uz.umarov.fcneftchi.ui.match_detail.adapter.MatchEventsAdapter

@AndroidEntryPoint
class MatchEventsFragment : Fragment() {

    private var _binding: FragmentMatchEventsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchDetailViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMatchEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val eventsAdapter = MatchEventsAdapter()
        binding.eventsRecyclerView.apply {
            adapter = eventsAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.gameDetail?.let { game ->
                    val playersMap = game.players.associate { it.player.id to "${it.player.firstName ?: ""} ${it.player.lastName}".trim() }
                    eventsAdapter.updateData(game.homeTeam.club.id, playersMap)
                    eventsAdapter.submitList(game.events.sortedBy { it.time })
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
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
import uz.umarov.fcneftchi.databinding.FragmentMatchLineupsBinding
import uz.umarov.fcneftchi.ui.MainActivity
import uz.umarov.fcneftchi.ui.match_detail.adapter.LineupAdapter

@AndroidEntryPoint
class MatchLineupsFragment : Fragment() {

    private var _binding: FragmentMatchLineupsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchDetailViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMatchLineupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeLineupAdapter = LineupAdapter()
        val awayLineupAdapter = LineupAdapter() 

        binding.homeLineupRecyclerView.apply {
            adapter = homeLineupAdapter
            layoutManager = LinearLayoutManager(context)
        }
        binding.awayLineupRecyclerView.apply {
            adapter = awayLineupAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.gameDetail?.let { game ->
                    homeLineupAdapter.submitList(game.players.filter { it.clubId == game.homeTeam.club.id })
                    awayLineupAdapter.submitList(game.players.filter { it.clubId == game.awayTeam.club.id })
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package uz.umarov.fcneftchi.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.databinding.FragmentStatisticsBinding
import uz.umarov.fcneftchi.ui.stats.adapter.PlayerStatsAdapter

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StatisticsViewModel by viewModels()
    private lateinit var playerStatsAdapter: PlayerStatsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state.isLoading
                binding.contentGroup.isVisible = !state.isLoading

                state.statsData?.let { data ->
                    binding.clubStatsCard.statMatches.text = data.clubStats.totalMatches.toString()
                    binding.clubStatsCard.statWins.text = data.clubStats.totalWins.toString()
                    binding.clubStatsCard.statDraws.text = data.clubStats.totalDraws.toString()
                    binding.clubStatsCard.statLosses.text = data.clubStats.totalLosses.toString()
                    binding.clubStatsCard.statGoalsScored.text =
                        data.clubStats.goalsScored.toString()
                    binding.clubStatsCard.statGoalsConceded.text =
                        data.clubStats.goalsConceded.toString()

                    playerStatsAdapter.submitList(data.playerStats)
                }
            }
        }
    }

    private fun setupRecyclerViews() {
        playerStatsAdapter = PlayerStatsAdapter()

        binding.playerStatsRecyclerView.apply {
            adapter = playerStatsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
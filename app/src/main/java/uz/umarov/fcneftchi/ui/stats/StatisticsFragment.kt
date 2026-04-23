package uz.umarov.fcneftchi.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.databinding.FragmentStatisticsBinding
import uz.umarov.fcneftchi.ui.MainActivity
import uz.umarov.fcneftchi.ui.stats.adapter.PlayerStatsAdapter
import uz.umarov.fcneftchi.util.applySystemBarPadding

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StatisticsViewModel by viewModels()
    private lateinit var playerStatsAdapter: PlayerStatsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarLayout.root.applySystemBarPadding(top = true)
        setupToolbar()
        setupRecyclerViews()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUi(state)
            }
        }
    }

    private fun updateUi(state: StatisticsUiState) {
        if (state.isLoading) {
            binding.shimmerContainer.startShimmer()
            binding.shimmerContainer.isVisible = true
            binding.contentScrollView.isVisible = false
            binding.stateView.hide()
            return
        }

        binding.shimmerContainer.animate()
            .alpha(0f)
            .setDuration(400)
            .withEndAction {
                if (_binding == null) return@withEndAction
                binding.shimmerContainer.stopShimmer()
                binding.shimmerContainer.isVisible = false
            }
            .start()

        when {
            state.error != null -> {
                binding.contentScrollView.isVisible = false
                binding.stateView.showError(onRetry = viewModel::retry)
            }
            state.statsData == null -> {
                binding.contentScrollView.isVisible = false
                binding.stateView.showEmpty(messageRes = uz.umarov.fcneftchi.R.string.state_empty_stats)
            }
            else -> {
                binding.stateView.hide()
                val data = state.statsData
                binding.clubStatsCard.statMatches.text = data.clubStats.totalMatches.toString()
                binding.clubStatsCard.statWins.text = data.clubStats.totalWins.toString()
                binding.clubStatsCard.statDraws.text = data.clubStats.totalDraws.toString()
                binding.clubStatsCard.statLosses.text = data.clubStats.totalLosses.toString()
                binding.clubStatsCard.statGoalsScored.text =
                    data.clubStats.goalsScored.toString()
                binding.clubStatsCard.statGoalsConceded.text =
                    data.clubStats.goalsConceded.toString()

                playerStatsAdapter.submitList(data.playerStats)

                binding.contentScrollView.alpha = 0f
                binding.contentScrollView.isVisible = true
                binding.contentScrollView.animate()
                    .alpha(1f)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .setDuration(500)
                    .start()
            }
        }
    }


    private fun setupToolbar() {
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerViews() {
        playerStatsAdapter = PlayerStatsAdapter()

        binding.playerStatsRecyclerView.apply {
            adapter = playerStatsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.hideMainUI()
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.showMainUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
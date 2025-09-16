package uz.umarov.fcneftchi.ui.match_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.databinding.FragmentMatchStatsBinding
import uz.umarov.fcneftchi.databinding.ItemMatchStatBinding

@AndroidEntryPoint
class MatchStatsFragment : Fragment() {

    private var _binding: FragmentMatchStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchDetailViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                // Only populate stats if they haven't been added yet
                if (state.gameDetail != null && binding.statsContainer.childCount == 0) {
                    state.gameDetail.statistics?.let { stats ->
                        val statList = listOf(
                            Triple("Shots", stats.shotsHome, stats.shotsAway),
                            Triple(
                                "Shots on Target",
                                stats.shotsOnTargetHome,
                                stats.shotsOnTargetAway
                            ),
                            Triple("Corners", stats.cornersHome, stats.cornersAway),
                            Triple("Offsides", stats.offsideHome, stats.offsideAway),
                            Triple("Fouls", stats.foulsHome, stats.foulsAway),
                            Triple("Yellow Cards", stats.yellowCardsHome, stats.yellowCardsAway)
                        )

                        statList.forEachIndexed { index, (label, home, away) ->
                            addStatView(label, home, away, index)
                        }
                    }
                }
            }
        }
    }

    private fun addStatView(label: String, homeValue: Int?, awayValue: Int?, index: Int) {
        val home = homeValue ?: 0
        val away = awayValue ?: 0
        val total = home + away

        val statBinding =
            ItemMatchStatBinding.inflate(layoutInflater, binding.statsContainer, false)
        statBinding.statLabel.text = label
        statBinding.homeStat.text = home.toString()
        statBinding.awayStat.text = away.toString()
        statBinding.statProgress.progress = if (total > 0) (home * 100 / total) else 50

        statBinding.root.alpha = 0f
        statBinding.root.translationY = 50f

        binding.statsContainer.addView(statBinding.root)

        statBinding.root.animate()
            .alpha(1f)
            .translationY(0f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(300)
            .setStartDelay(index * 60L)
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
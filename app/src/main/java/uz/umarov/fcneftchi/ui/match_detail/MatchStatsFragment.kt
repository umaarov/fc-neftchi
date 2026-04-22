package uz.umarov.fcneftchi.ui.match_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.GameStatistics
import uz.umarov.fcneftchi.databinding.FragmentMatchStatsBinding
import uz.umarov.fcneftchi.databinding.ItemMatchStatBinding
import uz.umarov.fcneftchi.ui.MainActivity

@AndroidEntryPoint
class MatchStatsFragment : Fragment() {

    private var _binding: FragmentMatchStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchDetailViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMatchStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.gameDetail?.statistics?.let { stats ->
                    binding.statsContainer.removeAllViews()
                    addStatView(getString(R.string.match_stat_shots), stats.shotsHome, stats.shotsAway)
                    addStatView(getString(R.string.match_stat_shots_on_target), stats.shotsOnTargetHome, stats.shotsOnTargetAway)
                    addStatView(getString(R.string.match_stat_corners), stats.cornersHome, stats.cornersAway)
                    addStatView(getString(R.string.match_stat_offsides), stats.offsideHome, stats.offsideAway)
                    addStatView(getString(R.string.match_stat_fouls), stats.foulsHome, stats.foulsAway)
                    addStatView(getString(R.string.match_stat_yellow_cards), stats.yellowCardsHome, stats.yellowCardsAway)
                    addStatView(getString(R.string.match_stat_red_cards), stats.redCardsHome, stats.redCardsAway)
                }
            }
        }
    }

    private fun addStatView(label: String, homeValue: Int?, awayValue: Int?) {
        val home = homeValue ?: 0
        val away = awayValue ?: 0
        val total = home + away

        val statBinding = ItemMatchStatBinding.inflate(layoutInflater, binding.statsContainer, false)
        statBinding.statLabel.text = label
        statBinding.homeStat.text = home.toString()
        statBinding.awayStat.text = away.toString()
        statBinding.statProgress.progress = if (total > 0) (home * 100 / total) else 50

        binding.statsContainer.addView(statBinding.root)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
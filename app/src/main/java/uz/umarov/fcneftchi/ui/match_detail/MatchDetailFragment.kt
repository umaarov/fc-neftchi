package uz.umarov.fcneftchi.ui.match_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.data.model.GameDetail
import uz.umarov.fcneftchi.databinding.FragmentMatchDetailBinding
import uz.umarov.fcneftchi.ui.match_detail.adapter.LineupAdapter
import uz.umarov.fcneftchi.ui.match_detail.adapter.MatchEventsAdapter
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class MatchDetailFragment : Fragment() {

    private var _binding: FragmentMatchDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state.isLoading
                binding.contentGroup.isVisible = !state.isLoading

                state.gameDetail?.let { game ->
                    binding.header.homeTeamLogo.load(game.homeTeam.club.logo)
                    binding.header.awayTeamLogo.load(game.awayTeam.club.logo)
                    binding.header.homeTeamName.text = game.homeTeam.club.title
                    binding.header.awayTeamName.text = game.awayTeam.club.title
                    binding.header.score.text = "${game.homeGoal} - ${game.awayGoal}"
                    binding.header.matchDate.text = formatMatchDate(game.startDate)

                    setupTabs(game)
                }
            }
        }
    }

    private fun setupTabs(game: GameDetail) {
        showEvents(game)

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showEvents(game)
                    1 -> showStats(game)
                    2 -> showLineups(game)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun showEvents(game: GameDetail) {
        binding.eventsView.root.isVisible = true
        binding.statsView.root.isVisible = false
        binding.lineupsView.root.isVisible = false

        val playersMap =
            game.players.associate { it.player.id to "${it.player.firstName ?: ""} ${it.player.lastName}".trim() }
        val eventsAdapter = MatchEventsAdapter(game.homeTeam.club.id, playersMap)
        binding.eventsView.eventsRecyclerView.apply {
            adapter = eventsAdapter
            layoutManager = LinearLayoutManager(context)
        }
        eventsAdapter.submitList(game.events.sortedBy { it.time })
    }

    private fun showStats(game: GameDetail) {
        binding.eventsView.root.isVisible = false
        binding.statsView.root.isVisible = true
        binding.lineupsView.root.isVisible = false

        game.statistics?.let { stats ->
            binding.statsView.statShots.text = "${stats.shotsHome} - ${stats.shotsAway}"
            binding.statsView.statShotsOnTarget.text =
                "${stats.shotsOnTargetHome} - ${stats.shotsOnTargetAway}"
            binding.statsView.statCorners.text = "${stats.cornersHome} - ${stats.cornersAway}"
            binding.statsView.statOffsides.text = "${stats.offsideHome} - ${stats.offsideAway}"
            binding.statsView.statFouls.text = "${stats.foulsHome} - ${stats.foulsAway}"
            binding.statsView.statYellowCards.text =
                "${stats.yellowCardsHome} - ${stats.yellowCardsAway}"
        }
    }

    private fun showLineups(game: GameDetail) {
        binding.eventsView.root.isVisible = false
        binding.statsView.root.isVisible = false
        binding.lineupsView.root.isVisible = true

        val homeLineupAdapter = LineupAdapter()
        val awayLineupAdapter = LineupAdapter()

        binding.lineupsView.homeLineupRecyclerView.apply {
            adapter = homeLineupAdapter
            layoutManager = LinearLayoutManager(context)
        }
        binding.lineupsView.awayLineupRecyclerView.apply {
            adapter = awayLineupAdapter
            layoutManager = LinearLayoutManager(context)
        }

        homeLineupAdapter.submitList(game.players.filter { it.clubId == game.homeTeam.club.id })
        awayLineupAdapter.submitList(game.players.filter { it.clubId == game.awayTeam.club.id })
    }

    private fun formatMatchDate(dateString: String?): String {
        if (dateString == null) return "N/A"
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val date = parser.parse(dateString)
            val formatter = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()).apply {
                timeZone = TimeZone.getDefault()
            }
            date?.let { formatter.format(it) } ?: "N/A"
        } catch (e: Exception) {
            "N/A"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
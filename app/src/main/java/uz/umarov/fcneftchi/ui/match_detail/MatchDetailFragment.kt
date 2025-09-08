package uz.umarov.fcneftchi.ui.match_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.databinding.FragmentMatchDetailBinding
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
        setHasOptionsMenu(true)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.header.toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowTitleEnabled(false)

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

                    setupViewPager()
                }
            }
        }
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = MatchDetailViewPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Events"
                1 -> "Stats"
                2 -> "Lineups"
                else -> null
            }
        }.attach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
            return true
        }
        return super.onOptionsItemSelected(item)
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
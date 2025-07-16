package uz.umarov.fcneftchi.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentHomeBinding
import uz.umarov.fcneftchi.databinding.ItemLastMatchBinding
import uz.umarov.fcneftchi.databinding.ItemNextMatchBinding
import uz.umarov.fcneftchi.ui.home.adapter.NewsHomeAdapter
import uz.umarov.fcneftchi.ui.home.adapter.StandingsAdapter
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var newsAdapter: NewsHomeAdapter
    private lateinit var standingsAdapter: StandingsAdapter

    private val countdownHandler = Handler(Looper.getMainLooper())
    private var countdownRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupClickListeners()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state.isLoading
                binding.contentGroup.isVisible = !state.isLoading

                state.nextMatch?.let {
                    bindNextMatch(binding.nextMatchCard, it)
                    startCountdown(it.matchDate)
                }
                state.lastMatch?.let { bindLastMatch(binding.lastMatchCard, it) }
                newsAdapter.submitList(state.news)
                standingsAdapter.submitList(state.standings)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadHomeData()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupRecyclerViews() {
        newsAdapter = NewsHomeAdapter { article ->
            val action = HomeFragmentDirections.actionHomeFragmentToNewsArticleFragment(article.url)
            findNavController().navigate(action)
        }
        binding.newsRecyclerView.apply {
            adapter = newsAdapter
            // Gorizontal scroll uchun
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        standingsAdapter = StandingsAdapter()
        binding.standingsRecyclerView.apply {
            adapter = standingsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupClickListeners() {
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        binding.viewAllNewsButton.setOnClickListener {
            bottomNav.selectedItemId = R.id.newsFragment
        }
        binding.viewAllStandingsButton.setOnClickListener {
            bottomNav.selectedItemId = R.id.matchesFragment
        }
        binding.nextMatchCard.root.setOnClickListener {
            viewModel.uiState.value.nextMatch?.let { match ->
                val action =
                    HomeFragmentDirections.actionHomeFragmentToMatchDetailFragment(match.id.toInt())
                findNavController().navigate(action)
            }
        }
        binding.lastMatchCard.root.setOnClickListener {
            viewModel.uiState.value.lastMatch?.let { match ->
                val action =
                    HomeFragmentDirections.actionHomeFragmentToMatchDetailFragment(match.id.toInt())
                findNavController().navigate(action)
            }
        }
    }

    private fun startCountdown(matchDateString: String?) {
        countdownRunnable?.let { countdownHandler.removeCallbacks(it) }
        if (matchDateString == null) return

        val matchDate = try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            parser.parse(matchDateString)
        } catch (e: Exception) {
            null
        } ?: return

        countdownRunnable = object : Runnable {
            override fun run() {
                val currentTime = System.currentTimeMillis()
                val diff = matchDate.time - currentTime

                if (diff > 0) {
                    val days = diff / (1000 * 60 * 60 * 24)
                    val hours = (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
                    val minutes = (diff % (1000 * 60 * 60)) / (1000 * 60)
                    val seconds = (diff % (1000 * 60)) / 1000
                    binding.nextMatchCard.countdownTimer.text =
                        String.format("%02d : %02d : %02d : %02d", days, hours, minutes, seconds)
                    countdownHandler.postDelayed(this, 1000)
                } else {
                    binding.nextMatchCard.countdownTimer.text = "MATCH STARTED"
                }
            }
        }
        countdownHandler.post(countdownRunnable!!)
    }

    private fun bindNextMatch(
        binding: ItemNextMatchBinding,
        match: uz.umarov.fcneftchi.data.model.Match
    ) {
        binding.homeTeamLogo.load(match.homeTeam.logoUrl)
        binding.awayTeamLogo.load(match.awayTeam.logoUrl)
        binding.homeTeamName.text = match.homeTeam.name
        binding.awayTeamName.text = match.awayTeam.name
        binding.matchDate.text = formatHomeMatchDate(match.matchDate)
        binding.matchCompetition.text = match.competition
    }

    private fun bindLastMatch(
        binding: ItemLastMatchBinding,
        match: uz.umarov.fcneftchi.data.model.Match
    ) {
        binding.homeTeamLogo.load(match.homeTeam.logoUrl)
        binding.awayTeamLogo.load(match.awayTeam.logoUrl)
        binding.homeTeamName.text = match.homeTeam.name
        binding.awayTeamName.text = match.awayTeam.name
        binding.homeTeamScore.text = match.homeScore.toString()
        binding.awayTeamScore.text = match.awayScore.toString()
        binding.matchCompetition.text = "${match.competition} - ${match.status}"
    }

    private fun formatHomeMatchDate(dateString: String?): String {
        if (dateString == null) return "N/A"
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val date = parser.parse(dateString)
            val formatter = SimpleDateFormat("dd MMM, HH:mm", Locale.ENGLISH).apply {
                timeZone = TimeZone.getDefault()
            }
            date?.let { formatter.format(it).toUpperCase(Locale.ROOT) } ?: "N/A"
        } catch (e: Exception) {
            "N/A"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownRunnable?.let { countdownHandler.removeCallbacks(it) }
        _binding = null
    }
}
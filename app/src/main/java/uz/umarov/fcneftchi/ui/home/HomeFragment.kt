package uz.umarov.fcneftchi.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.databinding.FragmentHomeBinding
import uz.umarov.fcneftchi.databinding.ItemLastMatchBinding
import uz.umarov.fcneftchi.databinding.ItemNextMatchBinding
import uz.umarov.fcneftchi.ui.home.adapter.NewsHomeAdapter
import uz.umarov.fcneftchi.ui.home.adapter.StandingsAdapter
import uz.umarov.fcneftchi.util.applySystemBarPadding

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var newsAdapter: NewsHomeAdapter
    private lateinit var standingsAdapter: StandingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state.isLoading
                binding.contentGroup.isVisible = !state.isLoading

                state.nextMatch?.let { bindNextMatch(binding.nextMatchCard, it) }
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
            val action = HomeFragmentDirections.actionHomeFragmentToNewsArticleFragment(article.id)
            findNavController().navigate(action)
        }
        binding.newsRecyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context)
        }

        standingsAdapter = StandingsAdapter()
        binding.standingsRecyclerView.apply {
            adapter = standingsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun bindNextMatch(
        binding: ItemNextMatchBinding,
        match: uz.umarov.fcneftchi.data.model.Match
    ) {
        binding.homeTeamLogo.load(match.homeTeam.logoUrl)
        binding.awayTeamLogo.load(match.awayTeam.logoUrl)
        binding.homeTeamName.text = match.homeTeam.name
        binding.awayTeamName.text = match.awayTeam.name
        binding.matchDate.text = "20 JUL, 19:00"
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package uz.umarov.fcneftchi.ui.table

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentLeagueTableBinding
import uz.umarov.fcneftchi.ui.table.adapter.LeagueTableAdapter

@AndroidEntryPoint
class LeagueTableFragment : Fragment() {

    private var _binding: FragmentLeagueTableBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LeagueTableViewModel by viewModels()
    private lateinit var tableAdapter: LeagueTableAdapter

    private var isSeasonSelectorSetup = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeagueTableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupLeagueSelector()

        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.retry() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUi(state)
            }
        }
    }

    private fun setupRecyclerView() {
        tableAdapter = LeagueTableAdapter()
        binding.tableRecyclerView.apply {
            adapter = tableAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupLeagueSelector() {
        val leagues = listOf("Superliga")
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown_season,
            leagues
        )
        binding.leagueSelectorInput.apply {
            setAdapter(adapter)
            setDropDownBackgroundResource(R.drawable.bg_dropdown_popup)
            setText("Superliga", false)

            setOnItemClickListener { _, _, _, _ ->
            }
        }
    }


    private fun setupSeasonSelector(seasons: Map<String, Int>) {
        val seasonNames = seasons.keys.toList()
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown_season,
            seasonNames
        )
        binding.seasonSelectorInput.apply {
            setAdapter(adapter)

            setDropDownBackgroundResource(R.drawable.bg_dropdown_popup)

            setOnItemClickListener { parent, _, position, _ ->
                val selectedSeason = parent.getItemAtPosition(position) as String
                if (selectedSeason != viewModel.uiState.value.selectedSeasonName) {
                    viewModel.changeSeason(selectedSeason)
                }
            }
        }
    }


    private fun updateUi(state: LeagueTableUiState) {
        if (!isSeasonSelectorSetup && state.availableSeasons.isNotEmpty()) {
            setupSeasonSelector(state.availableSeasons)
            isSeasonSelectorSetup = true
        }

        binding.seasonSelectorInput.setText(state.selectedSeasonName, false)

        if (!state.isLoading) {
            binding.swipeRefreshLayout.isRefreshing = false
        }

        val hasContent = tableAdapter.currentList.isNotEmpty()

        if (state.isLoading) {
            if (!hasContent) {
                binding.shimmerContainer.apply {
                    alpha = 1f
                    isVisible = true
                    startShimmer()
                }
                binding.contentGroup.isVisible = false
                binding.stateView.hide()
            }
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
                if (!hasContent) {
                    binding.contentGroup.isVisible = false
                    binding.stateView.showError(onRetry = viewModel::retry)
                }
            }
            state.standings.isEmpty() -> {
                binding.contentGroup.isVisible = false
                binding.stateView.showEmpty(messageRes = R.string.state_empty_standings)
            }
            else -> {
                binding.stateView.hide()
                val wasEmpty = !binding.contentGroup.isVisible
                tableAdapter.submitList(state.standings)

                binding.contentGroup.apply {
                    if (wasEmpty) {
                        alpha = 0f
                        isVisible = true
                        animate()
                            .alpha(1f)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setDuration(500)
                            .start()
                    } else {
                        isVisible = true
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

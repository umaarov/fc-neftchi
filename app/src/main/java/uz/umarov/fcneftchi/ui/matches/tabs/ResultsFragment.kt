package uz.umarov.fcneftchi.ui.matches.tabs

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
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentResultsBinding
import uz.umarov.fcneftchi.ui.matches.MatchesFragmentDirections
import uz.umarov.fcneftchi.ui.matches.adapter.ResultAdapter

@AndroidEntryPoint
class ResultsFragment : Fragment() {
    private var _binding: FragmentResultsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ResultsViewModel by viewModels()
    private lateinit var resultAdapter: ResultAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.retry() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUi(state)
            }
        }
    }

    private fun setupRecyclerView() {
        resultAdapter = ResultAdapter { match ->
            val action =
                MatchesFragmentDirections.actionMatchesFragmentToMatchDetailFragment(match.id.toInt())
            findNavController().navigate(action)
        }
        binding.resultsRecyclerView.apply {
            adapter = resultAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun updateUi(state: ResultsUiState) {
        if (!state.isLoading) {
            binding.swipeRefreshLayout.isRefreshing = false
        }

        val hasContent = resultAdapter.currentList.isNotEmpty()

        if (state.isLoading) {
            if (!hasContent) {
                binding.shimmerContainer.startShimmer()
                binding.shimmerContainer.isVisible = true
                binding.resultsRecyclerView.isVisible = false
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
                    binding.resultsRecyclerView.isVisible = false
                    binding.stateView.showError(onRetry = viewModel::retry)
                }
            }
            state.items.isEmpty() -> {
                binding.resultsRecyclerView.isVisible = false
                binding.stateView.showEmpty(messageRes = R.string.state_empty_results)
            }
            else -> {
                binding.stateView.hide()
                val wasEmpty = !binding.resultsRecyclerView.isVisible
                resultAdapter.submitList(state.items)
                binding.resultsRecyclerView.apply {
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

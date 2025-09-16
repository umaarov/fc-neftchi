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
        if (state.isLoading) {
            binding.shimmerContainer.startShimmer()
            binding.shimmerContainer.isVisible = true
            binding.resultsRecyclerView.isVisible = false
        } else {
            binding.shimmerContainer.animate()
                .alpha(0f)
                .setDuration(400)
                .withEndAction {
                    if (_binding == null) return@withEndAction
                    binding.shimmerContainer.stopShimmer()
                    binding.shimmerContainer.isVisible = false
                }
                .start()

            resultAdapter.submitList(state.items)
            binding.resultsRecyclerView.apply {
                alpha = 0f
                isVisible = true
                animate()
                    .alpha(1f)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .setDuration(500)
                    .start()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
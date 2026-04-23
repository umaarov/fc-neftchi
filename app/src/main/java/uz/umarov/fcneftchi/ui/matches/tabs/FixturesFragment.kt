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
import uz.umarov.fcneftchi.databinding.FragmentFixturesBinding
import uz.umarov.fcneftchi.ui.matches.MatchesFragmentDirections
import uz.umarov.fcneftchi.ui.matches.adapter.MatchAdapter

@AndroidEntryPoint
class FixturesFragment : Fragment() {
    private var _binding: FragmentFixturesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FixturesViewModel by viewModels()
    private lateinit var matchAdapter: MatchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFixturesBinding.inflate(inflater, container, false)
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
        matchAdapter = MatchAdapter { match ->
            val action = MatchesFragmentDirections.actionMatchesFragmentToMatchDetailFragment(match.id.toInt())
            findNavController().navigate(action)
        }
        binding.fixturesRecyclerView.apply {
            adapter = matchAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun updateUi(state: FixturesUiState) {
        if (state.isLoading) {
            binding.shimmerContainer.startShimmer()
            binding.shimmerContainer.isVisible = true
            binding.fixturesRecyclerView.isVisible = false
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
                binding.fixturesRecyclerView.isVisible = false
                binding.stateView.showError(onRetry = viewModel::retry)
            }
            state.items.isEmpty() -> {
                binding.fixturesRecyclerView.isVisible = false
                binding.stateView.showEmpty(messageRes = R.string.state_empty_fixtures)
            }
            else -> {
                binding.stateView.hide()
                matchAdapter.submitList(state.items)
                binding.fixturesRecyclerView.apply {
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

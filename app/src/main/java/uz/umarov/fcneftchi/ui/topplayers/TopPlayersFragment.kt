package uz.umarov.fcneftchi.ui.topplayers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentTopPlayersBinding
import uz.umarov.fcneftchi.ui.topplayers.adapter.TopPlayerAdapter

@AndroidEntryPoint
class TopPlayersFragment : Fragment() {

    private var _binding: FragmentTopPlayersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TopPlayersViewModel by viewModels()
    private lateinit var topPlayerAdapter: TopPlayerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopPlayersBinding.inflate(inflater, container, false)
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
        topPlayerAdapter = TopPlayerAdapter()
        binding.topPlayersRecyclerView.apply {
            adapter = topPlayerAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun updateUi(state: TopPlayersUiState) {
        if (state.isLoading) {
            binding.shimmerContainer.startShimmer()
            binding.shimmerContainer.isVisible = true
            binding.topPlayersRecyclerView.isVisible = false
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
                binding.topPlayersRecyclerView.isVisible = false
                binding.stateView.showError(onRetry = viewModel::retry)
            }
            state.items.isEmpty() -> {
                binding.topPlayersRecyclerView.isVisible = false
                binding.stateView.showEmpty(messageRes = R.string.state_empty_players)
            }
            else -> {
                binding.stateView.hide()
                topPlayerAdapter.submitList(state.items)
                binding.topPlayersRecyclerView.apply {
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

package uz.umarov.fcneftchi.ui.team

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
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentTeamBinding
import uz.umarov.fcneftchi.ui.MainActivity
import uz.umarov.fcneftchi.ui.team.adapter.PlayerAdapter
import uz.umarov.fcneftchi.ui.team.adapter.ShimmerPlayerAdapter
import uz.umarov.fcneftchi.util.applySystemBarPadding

@AndroidEntryPoint
class TeamFragment : Fragment() {

    private var _binding: FragmentTeamBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TeamViewModel by viewModels()
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var shimmerAdapter: ShimmerPlayerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarLayout.root.applySystemBarPadding(top = true)
        setupToolbar()
        setupRecyclerView()
        setupShimmerRecyclerView()

        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.retry() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUi(state)
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun setupRecyclerView() {
        playerAdapter = PlayerAdapter()
        val gridLayoutManager = GridLayoutManager(context, 2)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (playerAdapter.getItemViewType(position)) {
                    0 -> 2
                    1 -> 1
                    else -> 1
                }
            }
        }

        binding.teamRecyclerView.apply {
            adapter = playerAdapter
            layoutManager = gridLayoutManager
        }
    }

    private fun setupShimmerRecyclerView() {
        shimmerAdapter = ShimmerPlayerAdapter()
        val gridLayoutManager = GridLayoutManager(context, 2)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (shimmerAdapter.getItemViewType(position)) {
                    0 -> 2
                    1 -> 1
                    else -> 1
                }
            }
        }

        binding.shimmerRecyclerView.apply {
            adapter = shimmerAdapter
            layoutManager = gridLayoutManager
        }
    }

    private fun updateUi(state: TeamUiState) {
        if (!state.isLoading) {
            binding.swipeRefreshLayout.isRefreshing = false
        }

        val hasContent = playerAdapter.currentList.isNotEmpty()

        if (state.isLoading) {
            if (!hasContent) {
                binding.shimmerContainer.startShimmer()
                binding.shimmerContainer.isVisible = true
                binding.teamRecyclerView.isVisible = false
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
                    binding.teamRecyclerView.isVisible = false
                    binding.stateView.showError(onRetry = viewModel::retry)
                }
            }
            state.items.isEmpty() -> {
                binding.teamRecyclerView.isVisible = false
                binding.stateView.showEmpty(messageRes = R.string.state_empty_players)
            }
            else -> {
                binding.stateView.hide()
                val wasEmpty = !binding.teamRecyclerView.isVisible
                playerAdapter.submitList(state.items)
                binding.teamRecyclerView.apply {
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

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.hideMainUI()
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.showMainUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

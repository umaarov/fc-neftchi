package uz.umarov.fcneftchi.ui.videos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentVideosBinding
import uz.umarov.fcneftchi.ui.MainActivity
import uz.umarov.fcneftchi.ui.videos.adapter.VideoAdapter

@AndroidEntryPoint
class VideosFragment : Fragment() {

    private var _binding: FragmentVideosBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VideosViewModel by viewModels()
    private lateinit var videoAdapter: VideoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideosBinding.inflate(inflater, container, false)
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.event.collect { event ->
                if (event == null) return@collect
                val messageRes = when (event) {
                    VideosEvent.BookmarkAdded -> R.string.bookmark_added
                    VideosEvent.BookmarkRemoved -> R.string.bookmark_removed
                }
                Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show()
                viewModel.consumeEvent()
            }
        }
    }

    private fun setupRecyclerView() {
        videoAdapter = VideoAdapter(
            lifecycle = viewLifecycleOwner.lifecycle,
            onBookmarkClick = viewModel::toggleBookmark,
        )

        binding.videosRecyclerView.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
        }
    }

    private fun updateUi(state: VideosUiState) {
        if (!state.isLoading) {
            binding.swipeRefreshLayout.isRefreshing = false
        }

        val hasContent = videoAdapter.currentList.isNotEmpty()

        if (state.isLoading) {
            if (!hasContent) {
                binding.shimmerContainer.startShimmer()
                binding.shimmerContainer.isVisible = true
                binding.videosRecyclerView.isVisible = false
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
                    binding.videosRecyclerView.isVisible = false
                    binding.stateView.showError(onRetry = viewModel::retry)
                }
            }
            state.videos.isEmpty() -> {
                binding.videosRecyclerView.isVisible = false
                binding.stateView.showEmpty(messageRes = R.string.state_empty_videos)
            }
            else -> {
                binding.stateView.hide()
                val wasEmpty = !binding.videosRecyclerView.isVisible
                videoAdapter.submitList(state.videos) {
                    videoAdapter.submitBookmarkedIds(state.bookmarkedIds)
                }
                binding.videosRecyclerView.apply {
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
        (activity as? MainActivity)?.showMainUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.videosRecyclerView.adapter = null
        _binding = null
    }
}

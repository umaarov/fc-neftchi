package uz.umarov.fcneftchi.ui.videos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUi(state)
            }
        }
    }

    private fun setupRecyclerView() {
        videoAdapter = VideoAdapter { video ->
            try {
                val intent = Intent(Intent.ACTION_VIEW, video.videoUrl.toUri())
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Could not open video", Toast.LENGTH_SHORT).show()
            }
        }
        binding.videosRecyclerView.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun updateUi(state: VideosUiState) {
        if (state.isLoading) {
            binding.shimmerContainer.startShimmer()
            binding.shimmerContainer.isVisible = true
            binding.videosRecyclerView.isVisible = false
        } else {
            binding.shimmerContainer.animate()
                .alpha(0f)
                .setDuration(400)
                .withEndAction {
                    binding.shimmerContainer.stopShimmer()
                    binding.shimmerContainer.isVisible = false
                }
                .start()

            videoAdapter.submitList(state.videos)
            binding.videosRecyclerView.apply {
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

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showMainUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
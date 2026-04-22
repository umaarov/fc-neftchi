package uz.umarov.fcneftchi.ui.home

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
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentHomeBinding
import uz.umarov.fcneftchi.ui.MainActivity
import uz.umarov.fcneftchi.ui.home.adapter.HomeAdapter

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var homeAdapter: HomeAdapter

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
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUi(state)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadHomeData()
        }
    }

    private fun setupRecyclerView() {
        homeAdapter = HomeAdapter(
            lifecycleScope = viewLifecycleOwner.lifecycleScope,
            onNavigate = { destinationId -> findNavController().navigate(destinationId) },
            onArticleClick = { article ->
                val action =
                    HomeFragmentDirections.actionHomeFragmentToNewsArticleFragment(article.url)
                findNavController().navigate(action)
            },
            onMatchClick = { match ->
                val action =
                    HomeFragmentDirections.actionHomeFragmentToMatchDetailFragment(match.id.toInt())
                findNavController().navigate(action)
            },
            onVideoClick = { video ->
                try {
                    val intent = Intent(Intent.ACTION_VIEW, video.videoUrl.toUri())
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, R.string.could_not_open_video, Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.homeRecyclerView.adapter = homeAdapter
    }

    private fun updateUi(state: HomeUiState) {
        if (!state.isLoading) {
            binding.swipeRefreshLayout.isRefreshing = false
        }

        if (state.isLoading && homeAdapter.currentList.isEmpty()) {
            binding.shimmerContainer.startShimmer()
            binding.shimmerContainer.isVisible = true
            binding.homeRecyclerView.isVisible = false
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

            homeAdapter.submitList(state.items)
            binding.homeRecyclerView.apply {
                if (!isVisible) {
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

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showMainUI()
    }

    override fun onDestroyView() {
        binding.homeRecyclerView.adapter = null
        super.onDestroyView()
        _binding = null
    }
}
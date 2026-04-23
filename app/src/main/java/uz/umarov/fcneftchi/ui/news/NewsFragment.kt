package uz.umarov.fcneftchi.ui.news

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
import uz.umarov.fcneftchi.databinding.FragmentNewsBinding
import uz.umarov.fcneftchi.ui.MainActivity
import uz.umarov.fcneftchi.ui.news.adapter.NewsAdapter

@AndroidEntryPoint
class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewsViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.retry() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUI(state)
            }
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter { article ->
            val action = NewsFragmentDirections.actionNewsFragmentToNewsArticleFragment(article.url)
            findNavController().navigate(action)
        }

        binding.newsRecyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun updateUI(state: NewsUiState) {
        if (!state.isLoading) {
            binding.swipeRefreshLayout.isRefreshing = false
        }

        val hasContent = newsAdapter.currentList.isNotEmpty()
        val showShimmerForInitialLoad = state.isLoading && !hasContent

        if (!state.isLoading) {
            binding.shimmerContainer.animate()
                .alpha(0f)
                .setDuration(400)
                .withEndAction {
                    if (_binding == null) return@withEndAction
                    binding.shimmerContainer.stopShimmer()
                    binding.shimmerContainer.isVisible = false
                }
                .start()
        }

        when {
            showShimmerForInitialLoad -> {
                binding.stateView.hide()
                binding.newsRecyclerView.isVisible = false
                binding.shimmerContainer.alpha = 1f
                binding.shimmerContainer.isVisible = true
                binding.shimmerContainer.startShimmer()
            }
            state.isLoading -> {
                // Pull-to-refresh in progress; keep current content visible.
            }
            state.error != null -> {
                if (hasContent) {
                    binding.stateView.hide()
                } else {
                    binding.newsRecyclerView.isVisible = false
                    binding.stateView.showError(onRetry = viewModel::retry)
                }
            }
            state.articles.isEmpty() -> {
                binding.newsRecyclerView.isVisible = false
                binding.stateView.showEmpty(messageRes = R.string.state_empty_news)
            }
            else -> {
                binding.stateView.hide()
                val wasEmpty = !binding.newsRecyclerView.isVisible
                newsAdapter.submitList(state.articles)
                binding.newsRecyclerView.apply {
                    if (wasEmpty) {
                        alpha = 0f
                        translationY = 40f
                        isVisible = true
                        animate()
                            .alpha(1f)
                            .translationY(0f)
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
        _binding = null
    }
}

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
            binding.shimmerContainer.animate()
                .alpha(0f)
                .setDuration(400)
                .withEndAction {
                    binding.shimmerContainer.stopShimmer()
                    binding.shimmerContainer.isVisible = false
                }
                .start()
        }

        when {
            state.isLoading -> {
                binding.errorContainer.isVisible = false
                binding.newsRecyclerView.isVisible = false
                binding.shimmerContainer.alpha = 1f
                binding.shimmerContainer.isVisible = true
                binding.shimmerContainer.startShimmer()
            }

            state.error != null -> {
                binding.errorContainer.isVisible = true
            }

            state.articles.isEmpty() -> {
                binding.emptyContainer.isVisible = true
            }

            else -> {
                binding.newsRecyclerView.isVisible = true
                newsAdapter.submitList(state.articles)

                binding.newsRecyclerView.apply {
                    alpha = 0f
                    translationY = 40f
                    animate()
                        .alpha(1f)
                        .translationY(0f)
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
        super.onDestroyView()
        _binding = null
    }
}
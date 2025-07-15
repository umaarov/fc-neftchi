package uz.umarov.fcneftchi.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.databinding.FragmentNewsArticleBinding

@AndroidEntryPoint
class NewsArticleFragment : Fragment() {

    private var _binding: FragmentNewsArticleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsArticleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state.isLoading
                binding.contentScrollView.isVisible = !state.isLoading

                state.article?.let { article ->
                    binding.articleImage.load(article.imageUrl)
                    binding.articleTitle.text = article.title
                    binding.articleDate.text = article.date
                    binding.articleContent.text = article.content
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
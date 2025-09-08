package uz.umarov.fcneftchi.ui.news

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.databinding.FragmentNewsArticleBinding

@AndroidEntryPoint
class NewsArticleFragment : Fragment() {

    private var _binding: FragmentNewsArticleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsArticleViewModel by viewModels()
    private var currentArticle: NewsArticle? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state.isLoading
                binding.contentScrollView.isVisible = !state.isLoading && state.article != null

                state.article?.let { article ->
                    currentArticle = article
                    bindArticleData(article)
                }
            }
        }
    }

    private fun setupToolbar() {
        // This fragment now controls the toolbar
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)

        // Add menu items (e.g., Share button)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_article, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_share -> {
                        shareArticle()
                        true
                    }

                    android.R.id.home -> {
                        activity?.onBackPressedDispatcher?.onBackPressed()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun bindArticleData(article: NewsArticle) {
        binding.collapsingToolbar.title = article.title
        binding.articleImage.load(article.imageUrl) { crossfade(true) }
        binding.articleTitle.text = article.title
        binding.articleDate.text = article.date

        if (article.content.isNotBlank()) {
            val htmlContent =
                HtmlCompat.fromHtml(article.content, HtmlCompat.FROM_HTML_MODE_COMPACT)
            binding.articleContent.text = htmlContent
            binding.articleContent.movementMethod = LinkMovementMethod.getInstance()
        } else {
            binding.articleContent.text = "Ma'lumot topilmadi."
        }
    }

    private fun shareArticle() {
        currentArticle?.let { article ->
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, article.title)
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out this article from the FC Neftchi app: ${article.url}"
                )
            }
            startActivity(Intent.createChooser(intent, "Share Article"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
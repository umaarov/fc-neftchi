package uz.umarov.fcneftchi.ui.news

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import androidx.core.view.MenuProvider
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.databinding.FragmentNewsArticleBinding
import kotlin.math.roundToInt

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
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
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
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
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
        binding.articleTitleOverlay.text = article.title
        binding.articleDateOverlay.text = article.date

        if (article.content.isNotBlank()) {
            val imageGetter = coilImageGetter(binding.articleContent)
            val styledText =
                article.content.parseAsHtml(HtmlCompat.FROM_HTML_MODE_LEGACY, imageGetter)
            binding.articleContent.text = styledText
            binding.articleContent.movementMethod = LinkMovementMethod.getInstance()
        } else {
            binding.articleContent.text = "Ma'lumot topilmadi."
        }
    }

    private fun coilImageGetter(textView: TextView): Html.ImageGetter {
        return Html.ImageGetter { source ->
            val placeholder = createBitmap(1, 1).toDrawable(resources)

            lifecycleScope.launch {
                val request = ImageRequest.Builder(requireContext()).data(source).build()
                val result = requireContext().imageLoader.execute(request).drawable
                result?.let {
                    val screenWidth =
                        resources.displayMetrics.widthPixels - textView.paddingLeft - textView.paddingRight
                    val originalWidth = it.intrinsicWidth
                    val originalHeight = it.intrinsicHeight
                    val aspectRatio = originalWidth.toFloat() / originalHeight.toFloat()
                    val finalHeight = (screenWidth / aspectRatio).roundToInt()

                    it.setBounds(0, 0, screenWidth, finalHeight)
                    placeholder.setBounds(0, 0, screenWidth, finalHeight)

                    placeholder.bitmap?.let { bmp ->
                        val canvas = Canvas(bmp)
                        it.draw(canvas)
                    }

                    textView.text = textView.text
                }
            }
            placeholder
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
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
    }
}
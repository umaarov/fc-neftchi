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
import android.widget.Toast
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
import uz.umarov.fcneftchi.ui.MainActivity
import kotlin.math.roundToInt

@AndroidEntryPoint
class NewsArticleFragment : Fragment() {

    private var _binding: FragmentNewsArticleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsArticleViewModel by viewModels()
    private var currentArticle: NewsArticle? = null
    private var bookmarkMenuItem: MenuItem? = null

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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUi(state)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.event.collect { event ->
                if (event == null) return@collect
                val messageRes = when (event) {
                    NewsArticleEvent.BookmarkAdded -> R.string.bookmark_added
                    NewsArticleEvent.BookmarkRemoved -> R.string.bookmark_removed
                }
                Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show()
                viewModel.consumeEvent()
            }
        }
    }

    private fun updateUi(state: NewsArticleState) {
        if (state.isLoading) {
            binding.shimmerContainer.startShimmer()
            binding.shimmerContainer.isVisible = true
            binding.appBar.isVisible = false
            binding.contentScrollView.isVisible = false
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
                binding.appBar.isVisible = false
                binding.contentScrollView.isVisible = false
                binding.stateView.showError(onRetry = viewModel::retry)
            }
            state.article == null -> {
                binding.appBar.isVisible = false
                binding.contentScrollView.isVisible = false
                binding.stateView.showEmpty(messageRes = R.string.news_article_empty)
            }
            else -> {
                binding.stateView.hide()
                currentArticle = state.article
                bindArticleData(state.article)
                updateBookmarkIcon(state.isBookmarked)

                binding.appBar.alpha = 0f
                binding.contentScrollView.alpha = 0f
                binding.appBar.isVisible = true
                binding.contentScrollView.isVisible = true

                binding.appBar.animate().alpha(1f).setDuration(500).start()
                binding.contentScrollView.animate().alpha(1f).setDuration(500).setStartDelay(100)
                    .start()
            }
        }
    }

    private fun updateBookmarkIcon(isBookmarked: Boolean) {
        bookmarkMenuItem?.apply {
            setIcon(
                if (isBookmarked) R.drawable.ic_bookmark_filled
                else R.drawable.ic_bookmark_border
            )
            setTitle(if (isBookmarked) R.string.bookmark_remove else R.string.bookmark_add)
        }
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_article, menu)
                bookmarkMenuItem = menu.findItem(R.id.action_bookmark)
                updateBookmarkIcon(viewModel.uiState.value.isBookmarked)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_bookmark -> {
                        viewModel.toggleBookmark()
                        true
                    }

                    R.id.action_share -> {
//                        shareArticle()
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
            // binding.articleContent.movementMethod = LinkMovementMethod.getInstance()
        } else {
            binding.articleContent.text = getString(R.string.news_article_empty)
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
                    getString(R.string.news_share_message, article.url)
                )
            }
            startActivity(Intent.createChooser(intent, getString(R.string.news_share_title)))
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
        bookmarkMenuItem = null
        _binding = null
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
    }
}
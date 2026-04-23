package uz.umarov.fcneftchi.ui.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentBookmarksBinding
import uz.umarov.fcneftchi.ui.MainActivity
import uz.umarov.fcneftchi.util.applySystemBarPadding

class BookmarksFragment : Fragment() {

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarLayout.root.applySystemBarPadding(top = true)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.viewPager.adapter = BookmarksViewPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = getString(tabTitleRes(position))
        }.attach()
    }

    private fun tabTitleRes(position: Int): Int = when (position) {
        BOOKMARKS_ARTICLES_INDEX -> R.string.bookmarks_tab_articles
        BOOKMARKS_VIDEOS_INDEX -> R.string.bookmarks_tab_videos
        BOOKMARKS_PLAYERS_INDEX -> R.string.bookmarks_tab_players
        else -> R.string.title_bookmarks
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
        binding.viewPager.adapter = null
        _binding = null
    }
}

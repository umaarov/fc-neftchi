package uz.umarov.fcneftchi.ui.bookmarks

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

const val BOOKMARKS_ARTICLES_INDEX = 0
const val BOOKMARKS_VIDEOS_INDEX = 1
const val BOOKMARKS_PLAYERS_INDEX = 2

class BookmarksViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = when (position) {
        BOOKMARKS_ARTICLES_INDEX -> BookmarkedArticlesFragment()
        BOOKMARKS_VIDEOS_INDEX -> BookmarkedVideosFragment()
        BOOKMARKS_PLAYERS_INDEX -> BookmarkedPlayersFragment()
        else -> throw IndexOutOfBoundsException("Unknown bookmarks tab $position")
    }
}

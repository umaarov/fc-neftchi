package uz.umarov.fcneftchi.ui.match_detail

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

private const val NUM_TABS = 3

class MatchDetailViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = NUM_TABS

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MatchEventsFragment()
            1 -> MatchStatsFragment()
            else -> MatchLineupsFragment()
        }
    }
}
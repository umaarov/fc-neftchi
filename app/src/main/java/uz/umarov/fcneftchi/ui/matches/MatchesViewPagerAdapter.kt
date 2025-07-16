package uz.umarov.fcneftchi.ui.matches

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.umarov.fcneftchi.ui.matches.tabs.FixturesFragment
import uz.umarov.fcneftchi.ui.matches.tabs.ResultsFragment
import uz.umarov.fcneftchi.ui.table.LeagueTableFragment
import uz.umarov.fcneftchi.ui.topplayers.TopPlayersFragment

const val FIXTURES_PAGE_INDEX = 0
const val RESULTS_PAGE_INDEX = 1
const val TABLE_PAGE_INDEX = 2
const val TOP_PLAYERS_PAGE_INDEX = 3

class MatchesViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        FIXTURES_PAGE_INDEX to { FixturesFragment() },
        RESULTS_PAGE_INDEX to { ResultsFragment() },
        TABLE_PAGE_INDEX to { LeagueTableFragment() },
        TOP_PLAYERS_PAGE_INDEX to { TopPlayersFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}
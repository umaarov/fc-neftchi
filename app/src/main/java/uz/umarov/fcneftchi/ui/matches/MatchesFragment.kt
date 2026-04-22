package uz.umarov.fcneftchi.ui.matches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentMatchesBinding
import uz.umarov.fcneftchi.ui.MainActivity

class MatchesFragment : Fragment() {

    private var _binding: FragmentMatchesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        viewPager.isUserInputEnabled = false
        viewPager.adapter = MatchesViewPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()
    }

    private fun getTabTitle(position: Int): String? {
        val resId = when (position) {
            FIXTURES_PAGE_INDEX -> R.string.match_tab_fixtures
            RESULTS_PAGE_INDEX -> R.string.match_tab_results
            TABLE_PAGE_INDEX -> R.string.match_tab_table
            TOP_PLAYERS_PAGE_INDEX -> R.string.match_tab_top_players
            else -> return null
        }
        return getString(resId)
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
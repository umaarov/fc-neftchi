package uz.umarov.fcneftchi.ui.match_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.data.model.GameDetail
import uz.umarov.fcneftchi.databinding.FragmentMatchDetailBinding
import uz.umarov.fcneftchi.ui.MainActivity
import uz.umarov.fcneftchi.util.DateFormatter
import uz.umarov.fcneftchi.util.DateUtils
import uz.umarov.fcneftchi.util.applySystemBarPadding

@AndroidEntryPoint
class MatchDetailFragment : Fragment() {

    private var _binding: FragmentMatchDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding.toolbarLayout.root.applySystemBarPadding(top = true)
        setupToolbar()

        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowTitleEnabled(false)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUi(state)
            }
        }
    }

    private fun updateUi(state: MatchDetailUiState) {
        if (state.isLoading) {
            binding.shimmerContainer.startShimmer()
            binding.shimmerContainer.isVisible = true
            binding.contentGroup.isVisible = false
        } else {
            binding.shimmerContainer.animate()
                .alpha(0f)
                .setDuration(400)
                .withEndAction {
                    if (_binding == null) return@withEndAction
                    binding.shimmerContainer.stopShimmer()
                    binding.shimmerContainer.isVisible = false
                }
                .start()

            state.gameDetail?.let { game ->
                bindHeaderData(game)
                setupViewPager()
            }
            binding.contentGroup.alpha = 0f
            binding.contentGroup.isVisible = true
            binding.contentGroup.animate()
                .alpha(1f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setDuration(500)
                .start()
        }
    }

    private fun bindHeaderData(game: GameDetail) {
        binding.header.homeTeamLogo.load(game.homeTeam.club.logo)
        binding.header.awayTeamLogo.load(game.awayTeam.club.logo)
        binding.header.homeTeamName.text = game.homeTeam.club.title
        binding.header.awayTeamName.text = game.awayTeam.club.title
        binding.header.score.text = "${game.homeGoal} - ${game.awayGoal}"
        binding.header.matchDate.text = formatMatchDate(game.startDate)
    }

    private fun setupToolbar() {
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupViewPager() {
        if (binding.viewPager.adapter == null) {
            binding.viewPager.adapter = MatchDetailViewPagerAdapter(this)
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Events"
                    1 -> "Stats"
                    2 -> "Lineups"
                    else -> null
                }
            }.attach()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun formatMatchDate(dateString: String?): String {
        val date = DateUtils.parseDate(dateString) ?: return "N/A"
        return DateFormatter.formatMatchDetailDate(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
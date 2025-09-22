package uz.umarov.fcneftchi.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentPlayerProfileBinding
import uz.umarov.fcneftchi.ui.MainActivity
import uz.umarov.fcneftchi.ui.player.adapter.PlayerCareerAdapter
import uz.umarov.fcneftchi.util.applySystemBarPadding
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class PlayerProfileFragment : Fragment() {

    private var _binding: FragmentPlayerProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerProfileViewModel by viewModels()
    private lateinit var careerAdapter: PlayerCareerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarLayout.root.applySystemBarPadding(top = true)
        setupToolbar()
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUi(state)
            }
        }
    }

    private fun updateUi(state: PlayerProfileUiState) {
        if (state.isLoading) {
            binding.shimmerContainer.startShimmer()
            binding.shimmerContainer.isVisible = true
            binding.contentScrollView.isVisible = false
        } else {
            binding.shimmerContainer.animate()
                .alpha(0f)
                .setDuration(400)
                .withEndAction {
                    binding.shimmerContainer.stopShimmer()
                    binding.shimmerContainer.isVisible = false
                }
                .start()

            state.profile?.let { profile ->
                bindProfileData(profile)

                binding.contentScrollView.alpha = 0f
                binding.contentScrollView.isVisible = true
                binding.contentScrollView.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .start()
            }
        }
    }

    private fun bindProfileData(profile: uz.umarov.fcneftchi.data.model.PlayerProfile) {
        val fullName = "${profile.details.firstName} ${profile.details.lastName}".trim()
        binding.playerImage.load(profile.details.photo) {
            placeholder(R.drawable.player_placeholder)
            error(R.drawable.player_placeholder)
        }
        binding.playerName.text = fullName
        binding.playerNumber.text = "#${profile.details.number}"
        binding.bioPosition.statLabel.text = "POSITION"
        binding.bioPosition.statValue.text = mapPosition(profile.details.position)

        binding.bioCountry.statLabel.text = "COUNTRY"
        binding.bioCountry.statValue.text = profile.details.country.title

        binding.bioAge.statLabel.text = "AGE"
        binding.bioAge.statValue.text = calculateAge(profile.details.birthday)
        val stats = binding.playerStatsCard
        stats.statGames.statLabel.text = "GAMES"
        stats.statGames.statValue.text = profile.stats.games.toString()

        stats.statGoals.statLabel.text = "GOALS"
        stats.statGoals.statValue.text = profile.stats.goals.toString()

        stats.statAssists.statLabel.text = "ASSISTS"
        stats.statAssists.statValue.text = profile.stats.assists.toString()

        stats.statMinutes.statLabel.text = "MINUTES"
        stats.statMinutes.statValue.text = profile.stats.minutes.toString()

        stats.statYellow.statLabel.text = "YELLOW"
        stats.statYellow.statValue.text = profile.stats.yellowCards.toString()

        stats.statRed.statLabel.text = "RED"
        stats.statRed.statValue.text = profile.stats.redCards.toString()

        careerAdapter.submitList(profile.career)
    }

    private fun setupToolbar() {
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        careerAdapter = PlayerCareerAdapter()
        binding.careerRecyclerView.apply {
            adapter = careerAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun mapPosition(positionId: Int): String {
        return when (positionId) {
            1 -> "Goalkeeper"
            2 -> "Defender"
            3 -> "Midfielder"
            4 -> "Forward"
            else -> "Unknown"
        }
    }

    private fun calculateAge(birthdayString: String?): String {
        if (birthdayString == null) return "N/A"
        return try {
            val birthDate = LocalDate.parse(birthdayString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            Period.between(birthDate, LocalDate.now()).years.toString()
        } catch (e: Exception) {
            "N/A"
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
        _binding = null
    }
}
package uz.umarov.fcneftchi.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.databinding.FragmentPlayerProfileBinding
import uz.umarov.fcneftchi.ui.player.adapter.PlayerCareerAdapter
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
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state.isLoading
                binding.contentScrollView.isVisible = !state.isLoading

                state.profile?.let { profile ->
                    binding.playerBioCard.playerImage.load(profile.details.photo)
                    binding.playerBioCard.playerName.text =
                        "${profile.details.firstName} ${profile.details.lastName}"
                    binding.playerBioCard.playerNumber.text = "#${profile.details.number}"
                    binding.playerBioCard.playerPosition.text =
                        mapPosition(profile.details.position)
                    binding.playerBioCard.playerCountry.text = profile.details.country.title
                    binding.playerBioCard.playerAge.text = calculateAge(profile.details.birthday)

                    binding.playerStatsCard.statGames.text = profile.stats.games.toString()
                    binding.playerStatsCard.statMinutes.text = profile.stats.minutes.toString()
                    binding.playerStatsCard.statGoals.text = profile.stats.goals.toString()
                    binding.playerStatsCard.statAssists.text = profile.stats.assists.toString()
                    binding.playerStatsCard.statYellow.text = profile.stats.yellowCards.toString()
                    binding.playerStatsCard.statRed.text = profile.stats.redCards.toString()

                    careerAdapter.submitList(profile.career)
                }
            }
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
            val age = Period.between(birthDate, LocalDate.now()).years
            "$age years old"
        } catch (e: Exception) {
            "N/A"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
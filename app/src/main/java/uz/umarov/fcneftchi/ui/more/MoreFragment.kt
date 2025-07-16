package uz.umarov.fcneftchi.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentMoreBinding

class MoreFragment : Fragment() {
    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.teamButton.setOnClickListener {
            findNavController().navigate(R.id.action_moreFragment_to_teamFragment)
        }

        binding.statisticsButton.setOnClickListener {
            findNavController().navigate(R.id.action_moreFragment_to_statisticsFragment)
        }

        binding.leagueTableButton.setOnClickListener {
            findNavController().navigate(R.id.action_moreFragment_to_leagueTableFragment)
        }

        binding.clubHistoryButton.setOnClickListener {
            findNavController().navigate(R.id.action_moreFragment_to_clubHistoryFragment)
        }

        binding.stadiumButton.setOnClickListener {
            findNavController().navigate(R.id.action_moreFragment_to_stadiumFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
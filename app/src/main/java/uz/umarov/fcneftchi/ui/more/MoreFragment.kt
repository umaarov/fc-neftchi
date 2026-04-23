package uz.umarov.fcneftchi.ui.more

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentMoreBinding
import uz.umarov.fcneftchi.ui.MainActivity

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

        binding.clubHistoryButton.setOnClickListener {
            findNavController().navigate(R.id.action_moreFragment_to_clubHistoryFragment)
        }

        binding.stadiumButton.setOnClickListener {
            findNavController().navigate(R.id.action_moreFragment_to_stadiumFragment)
        }

        binding.bookmarksButton.setOnClickListener {
            findNavController().navigate(R.id.action_moreFragment_to_bookmarksFragment)
        }

        binding.settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_moreFragment_to_settingsFragment)
        }

        binding.telegramButton.setOnClickListener { openUrl("https://t.me/fcneftchiuz") }
        binding.instagramButton.setOnClickListener { openUrl("https://www.instagram.com/fcfarneftchi/") }
        binding.youtubeButton.setOnClickListener { openUrl("https://www.youtube.com/c/FCNEFTCHI") }
        binding.facebookButton.setOnClickListener { openUrl("https://www.facebook.com/fcneftchi.uz/") }

    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.hideToolbarOnly()
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
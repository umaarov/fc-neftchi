package uz.umarov.fcneftchi.ui.socials

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import uz.umarov.fcneftchi.databinding.FragmentSocialsBinding

class SocialsFragment : Fragment() {

    private var _binding: FragmentSocialsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSocialsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.telegramButton.setOnClickListener {
            openUrl("https://t.me/fcneftchiuz")
        }
        binding.instagramButton.setOnClickListener {
            openUrl("https://www.instagram.com/fcfarneftchi/")
        }
        binding.youtubeButton.setOnClickListener {
            openUrl("https://www.youtube.com/c/FCNEFTCHI")
        }
        binding.facebookButton.setOnClickListener {
            openUrl("https://www.facebook.com/fcneftchi.uz/")
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
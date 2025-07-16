package uz.umarov.fcneftchi.ui.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentClubHistoryBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class ClubHistoryFragment : Fragment() {

    private var _binding: FragmentClubHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClubHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        loadHistoryIntoWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadHistoryIntoWebView() {
        try {
            val inputStream = resources.openRawResource(R.raw.club_history)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val content = reader.readText()

            binding.webView.apply {
                settings.javaScriptEnabled = true
                setBackgroundColor(0x00000000)
                loadDataWithBaseURL(null, content, "text/html", "UTF-8", null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val errorHtml = "<html><body style='color:red;'>Tarixni yuklashda xatolik yuz berdi.</body></html>"
            binding.webView.loadData(errorHtml, "text/html", "UTF-8")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
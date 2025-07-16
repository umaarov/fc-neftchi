package uz.umarov.fcneftchi.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.umarov.fcneftchi.databinding.FragmentSettingsBinding
import uz.umarov.fcneftchi.util.SharedPrefsHelper
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var prefsHelper: SharedPrefsHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupCurrentSettings()
        setupClickListeners()
    }

    private fun setupCurrentSettings() {
        binding.matchReminderSwitch.isChecked =
            prefsHelper.getBoolean(SharedPrefsHelper.PREF_NOTIF_MATCH_REMINDER)
        binding.newsAlertSwitch.isChecked =
            prefsHelper.getBoolean(SharedPrefsHelper.PREF_NOTIF_NEWS_ALERTS)
    }

    private fun setupClickListeners() {
        binding.matchReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefsHelper.setBoolean(SharedPrefsHelper.PREF_NOTIF_MATCH_REMINDER, isChecked)
        }

        binding.newsAlertSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefsHelper.setBoolean(SharedPrefsHelper.PREF_NOTIF_NEWS_ALERTS, isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
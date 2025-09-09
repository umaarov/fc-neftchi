package uz.umarov.fcneftchi.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import uz.umarov.fcneftchi.databinding.FragmentSettingsBinding
import uz.umarov.fcneftchi.ui.MainActivity
import uz.umarov.fcneftchi.util.SharedPrefsHelper
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var prefsHelper: SharedPrefsHelper

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(context, "Bildirishnomalar yoqildi!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Bildirishnomalarga ruxsat berilmadi.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        askNotificationPermission()
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
            updateTopicSubscription("match_reminders", isChecked)
        }

        binding.newsAlertSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefsHelper.setBoolean(SharedPrefsHelper.PREF_NOTIF_NEWS_ALERTS, isChecked)
            updateTopicSubscription("news_alerts", isChecked)
        }
    }

    private fun updateTopicSubscription(topic: String, subscribe: Boolean) {
        val messaging = FirebaseMessaging.getInstance()
        if (subscribe) {
            messaging.subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    val msg =
                        if (task.isSuccessful) "Obuna muvaffaqiyatli!" else "Obuna bo'lishda xatolik."
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
        } else {
            messaging.unsubscribeFromTopic(topic)
                .addOnCompleteListener { task ->
                    val msg =
                        if (task.isSuccessful) "Obuna bekor qilindi!" else "Bekor qilishda xatolik."
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
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
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentSettingsBinding
import uz.umarov.fcneftchi.ui.MainActivity
import uz.umarov.fcneftchi.util.SharedPrefsHelper
import uz.umarov.fcneftchi.util.ThemeManager
import uz.umarov.fcneftchi.util.applySystemBarPadding
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var prefsHelper: SharedPrefsHelper

    @Inject
    lateinit var themeManager: ThemeManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        val msgRes =
            if (isGranted) R.string.notif_permission_granted else R.string.notif_permission_denied
        Toast.makeText(context, msgRes, Toast.LENGTH_SHORT).show()
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
        binding.toolbarLayout.root.applySystemBarPadding(top = true)
        setupToolbar()
        askNotificationPermission()
        setupCurrentSettings()
        setupClickListeners()
    }

    private fun setupToolbar() {
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupCurrentSettings() {
        binding.matchReminderSwitch.isChecked =
            prefsHelper.getBoolean(SharedPrefsHelper.PREF_NOTIF_MATCH_REMINDER)
        binding.newsAlertSwitch.isChecked =
            prefsHelper.getBoolean(SharedPrefsHelper.PREF_NOTIF_NEWS_ALERTS)

        val buttonIdForTheme = when (themeManager.currentTheme()) {
            ThemeManager.Theme.LIGHT -> R.id.theme_light_button
            ThemeManager.Theme.DARK -> R.id.theme_dark_button
            ThemeManager.Theme.SYSTEM -> R.id.theme_system_button
        }
        binding.themeToggleGroup.check(buttonIdForTheme)
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

        binding.themeToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val theme = when (checkedId) {
                R.id.theme_light_button -> ThemeManager.Theme.LIGHT
                R.id.theme_dark_button -> ThemeManager.Theme.DARK
                R.id.theme_system_button -> ThemeManager.Theme.SYSTEM
                else -> return@addOnButtonCheckedListener
            }
            if (theme != themeManager.currentTheme()) {
                themeManager.apply(theme)
            }
        }
    }

    private fun updateTopicSubscription(topic: String, subscribe: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            val messaging = FirebaseMessaging.getInstance()
            val result = runCatching {
                if (subscribe) {
                    messaging.subscribeToTopic(topic).await()
                } else {
                    messaging.unsubscribeFromTopic(topic).await()
                }
            }
            val msgRes = when {
                result.isSuccess && subscribe -> R.string.notif_subscribe_success
                result.isSuccess && !subscribe -> R.string.notif_unsubscribe_success
                !result.isSuccess && subscribe -> R.string.notif_subscribe_failed
                else -> R.string.notif_unsubscribe_failed
            }
            Toast.makeText(context, msgRes, Toast.LENGTH_SHORT).show()
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

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
import uz.umarov.fcneftchi.databinding.ItemNotificationToggleBinding
import uz.umarov.fcneftchi.notifications.NotificationCategory
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
        setupNotificationToggles()
        setupThemeToggle()
    }

    private fun setupToolbar() {
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupNotificationToggles() {
        val inflater = LayoutInflater.from(requireContext())
        val container = binding.notificationTogglesContainer
        container.removeAllViews()
        NotificationCategory.entries.forEachIndexed { index, category ->
            val rowBinding =
                ItemNotificationToggleBinding.inflate(inflater, container, false)
            rowBinding.toggleTitle.setText(category.titleRes)
            rowBinding.toggleSubtitle.setText(category.descriptionRes)
            rowBinding.toggleSwitch.isChecked =
                prefsHelper.getBoolean(category.prefKey, category.defaultEnabled)
            rowBinding.toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
                prefsHelper.setBoolean(category.prefKey, isChecked)
                updateTopicSubscription(category, isChecked)
            }
            container.addView(rowBinding.root)
            if (index != NotificationCategory.entries.lastIndex) {
                container.addView(createDivider())
            }
        }
    }

    private fun createDivider(): View = View(requireContext()).apply {
        val density = resources.displayMetrics.density
        layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            (1 * density).toInt().coerceAtLeast(1)
        ).apply {
            val horizontal = (16 * density).toInt()
            setMargins(horizontal, 0, horizontal, 0)
        }
        setBackgroundResource(R.color.divider)
    }

    private fun setupThemeToggle() {
        val buttonIdForTheme = when (themeManager.currentTheme()) {
            ThemeManager.Theme.LIGHT -> R.id.theme_light_button
            ThemeManager.Theme.DARK -> R.id.theme_dark_button
            ThemeManager.Theme.SYSTEM -> R.id.theme_system_button
        }
        binding.themeToggleGroup.check(buttonIdForTheme)

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

    private fun updateTopicSubscription(category: NotificationCategory, subscribe: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            val messaging = FirebaseMessaging.getInstance()
            val result = runCatching {
                if (subscribe) {
                    messaging.subscribeToTopic(category.topic).await()
                } else {
                    messaging.unsubscribeFromTopic(category.topic).await()
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

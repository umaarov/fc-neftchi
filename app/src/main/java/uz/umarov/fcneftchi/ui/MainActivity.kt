package uz.umarov.fcneftchi.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.metrics.performance.JankStats
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uz.umarov.fcneftchi.BuildConfig
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.ActivityMainBinding
import uz.umarov.fcneftchi.util.applySystemBarPadding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels()

    private lateinit var jankStats: JankStats

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            subscribeToDefaultTopic()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

//        splashScreen.setKeepOnScreenCondition {
//            !viewModel.isReady.value
//        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appBarLayout.applySystemBarPadding(top = true)
        binding.tabLayout.applySystemBarPadding(bottom = true)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setOf(
            R.id.homeFragment,
            R.id.newsFragment,
            R.id.matchesFragment,
            R.id.videosFragment,
            R.id.moreFragment
        )

        setupTabLayoutWithNavController()

        askNotificationPermission()

        fetchFcmTokenForDebug()

        val jankFrameListener = JankStats.OnFrameListener { frameData ->
            if (frameData.isJank) {
                Log.w("JankStats", "Janky frame detected: $frameData")
            }
        }

        jankStats = JankStats.createAndTrack(window, jankFrameListener)
    }

    private fun fetchFcmTokenForDebug() {
        if (!BuildConfig.DEBUG) return
        lifecycleScope.launch {
            runCatching { FirebaseMessaging.getInstance().token.await() }
                .onSuccess { token -> Log.d("FCM_TOKEN", token) }
                .onFailure { e -> Log.w("FCM_TOKEN", "Failed to fetch FCM token", e) }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    subscribeToDefaultTopic()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    AlertDialog.Builder(this)
                        .setTitle("Stay Updated!")
                        .setMessage("Please allow notifications to get live match scores, breaking news, and video updates directly on your phone.")
                        .setPositiveButton("Allow") { _, _ ->
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        .setNegativeButton("Later", null)
                        .show()
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            subscribeToDefaultTopic()
        }
    }

    private fun subscribeToDefaultTopic() {
        lifecycleScope.launch {
            runCatching { FirebaseMessaging.getInstance().subscribeToTopic("news").await() }
                .onFailure { e ->
                    if (BuildConfig.DEBUG) Log.w("FCM_TOPIC", "subscribe failed", e)
                }
        }
    }

    override fun onResume() {
        super.onResume()
        jankStats.isTrackingEnabled = true
    }

    override fun onPause() {
        super.onPause()
        jankStats.isTrackingEnabled = false
    }

    private fun setupTabLayoutWithNavController() {
        val destinations = listOf(
            Triple(R.id.homeFragment, "Home", R.drawable.ic_home),
            Triple(R.id.newsFragment, "News", R.drawable.ic_news),
            Triple(R.id.matchesFragment, "Matches", R.drawable.ic_matches),
            Triple(R.id.videosFragment, "Videos", R.drawable.ic_videos),
            Triple(R.id.moreFragment, "More", R.drawable.ic_more)
        )

        destinations.forEach { (destinationId, label, iconId) ->
            val tab = binding.tabLayout.newTab().apply {
                text = label
                setIcon(iconId)
                tag = destinationId
            }
            binding.tabLayout.addTab(tab)
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val destinationId = tab?.tag as? Int ?: return
                if (navController.currentDestination?.id != destinationId) {
                    navController.navigate(destinationId)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        navController.addOnDestinationChangedListener { _, destination, _ ->
            for (i in 0 until binding.tabLayout.tabCount) {
                val tab = binding.tabLayout.getTabAt(i)
                if (tab?.tag as? Int == destination.id) {
                    tab.select()
                    break
                }
            }
        }
    }

    fun showMainUI() {
        binding.appBarLayout.isVisible = true
        binding.tabLayout.isVisible = true
    }

    fun hideMainUI() {
        binding.appBarLayout.isVisible = false
        binding.tabLayout.isVisible = false
    }

    fun hideToolbarOnly() {
        binding.appBarLayout.isVisible = false
        binding.tabLayout.isVisible = true
    }
}
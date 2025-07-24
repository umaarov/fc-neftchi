package uz.umarov.fcneftchi.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.ActivityMainBinding
import uz.umarov.fcneftchi.util.applySystemBarPadding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appBarLayout.applySystemBarPadding(top = true)
        binding.bottomNavView.applySystemBarPadding(bottom = true) // Apply padding to the correct view

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val topLevelDestinations = setOf(
            R.id.homeFragment,
            R.id.matchesFragment,
            R.id.newsFragment,
            R.id.videosFragment,
            R.id.moreFragment
        )
        val appBarConfiguration = AppBarConfiguration(topLevelDestinations)

        // Setup Toolbar and BottomNav with NavController
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.bottomNavView.setupWithNavController(navController) // The simple, correct way

        // Show/Hide bottom nav based on destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavView.isVisible = destination.id in topLevelDestinations
        }

        // Firebase Token Logic
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_TOKEN", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM_TOKEN", token)
        }
    }
}
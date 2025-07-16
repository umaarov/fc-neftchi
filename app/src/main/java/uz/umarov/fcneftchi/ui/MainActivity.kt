package uz.umarov.fcneftchi.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.ActivityMainBinding
import uz.umarov.fcneftchi.util.applySystemBarPadding
import uz.umarov.fcneftchi.util.slideDown
import uz.umarov.fcneftchi.util.slideUp

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appBarLayout.applySystemBarPadding(top = true)
        binding.bottomNavView.applySystemBarPadding(bottom = true)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val topLevelDestinations = setOf(
            R.id.homeFragment,
            R.id.matchesFragment,
            R.id.newsFragment,
            R.id.videosFragment,
            R.id.moreFragment
        )

        appBarConfiguration = AppBarConfiguration(topLevelDestinations)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.bottomNavView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isTopLevelDestination = topLevelDestinations.contains(destination.id)

            if (isTopLevelDestination) {
                binding.bottomNavView.slideUp()
            } else {
                binding.bottomNavView.slideDown()
            }
        }

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
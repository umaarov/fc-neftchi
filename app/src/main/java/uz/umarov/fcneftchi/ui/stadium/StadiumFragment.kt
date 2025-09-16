package uz.umarov.fcneftchi.ui.stadium

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentStadiumBinding
import uz.umarov.fcneftchi.ui.MainActivity
import uz.umarov.fcneftchi.ui.stadium.adapter.IndicatorAdapter
import uz.umarov.fcneftchi.ui.stadium.adapter.StadiumImagePagerAdapter
import kotlin.math.abs

class StadiumFragment : Fragment() {

    private var _binding: FragmentStadiumBinding? = null
    private val binding get() = _binding!!

    private lateinit var autoScrollHandler: Handler
    private var autoScrollRunnable: Runnable? = null
    private val AUTO_SCROLL_DELAY = 5000L

    private val contentRevealHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStadiumBinding.inflate(inflater, container, false)
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        autoScrollHandler = Handler(Looper.getMainLooper())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.shimmerContainer.startShimmer()
        binding.shimmerContainer.isVisible = true

        contentRevealHandler.postDelayed({
            setupToolbar()
            setupImageCarousel()
            setupContent()
            animateContentIn()
        }, 500)
    }

    private fun animateContentIn() {
        binding.shimmerContainer.animate()
            .alpha(0f)
            .setDuration(400)
            .withEndAction {
                binding.shimmerContainer.stopShimmer()
                binding.shimmerContainer.isVisible = false
            }
            .start()

        binding.appBar.alpha = 0f
        binding.appBar.isVisible = true
        binding.appBar.animate().alpha(1f).setDuration(500).start()

        binding.contentScrollView.alpha = 0f
        binding.contentScrollView.isVisible = true
        binding.contentScrollView.animate().alpha(1f).setDuration(500).start()
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(
                menu: android.view.Menu,
                menuInflater: android.view.MenuInflater
            ) {
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        activity?.onBackPressedDispatcher?.onBackPressed()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupImageCarousel() {
        val stadiumImages = listOf(
            R.drawable.img_stadium,
            R.drawable.img_stadium_2,
            R.drawable.img_stadium_3
        )

        val pagerAdapter = StadiumImagePagerAdapter(stadiumImages)
        binding.stadiumImagePager.adapter = pagerAdapter

        val indicatorAdapter = IndicatorAdapter(stadiumImages.size)
        binding.stadiumImageIndicator.adapter = indicatorAdapter

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        binding.stadiumImagePager.setPageTransformer(compositePageTransformer)

        binding.stadiumImagePager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                indicatorAdapter.selectedPosition = position
                indicatorAdapter.notifyDataSetChanged()
            }
        })

        startAutoScroll(stadiumImages.size)
    }

    private fun setupContent() {
        val stadiumName = "Istiqlol Stadium"
        binding.collapsingToolbar.title = stadiumName
        binding.stadiumName.text = stadiumName
        binding.stadiumAddress.text = getString(R.string.stadium_address)
        binding.stadiumCapacity.text = "20,000"
        binding.stadiumOpenedYear.text = "2015"
        binding.stadiumDescription.text = getString(R.string.stadium_description)

        val locationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_on)
        binding.stadiumAddress.setCompoundDrawablesWithIntrinsicBounds(
            locationIcon,
            null,
            null,
            null
        )
    }

    private fun startAutoScroll(itemCount: Int) {
        clearCarouselTimer()
        if (itemCount <= 1) return

        autoScrollRunnable = object : Runnable {
            override fun run() {
                val currentItem = binding.stadiumImagePager.currentItem
                val nextItem = (currentItem + 1) % itemCount
                binding.stadiumImagePager.setCurrentItem(nextItem, true)
                autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY)
            }
        }
        autoScrollHandler.postDelayed(autoScrollRunnable!!, AUTO_SCROLL_DELAY)
    }

    private fun clearCarouselTimer() {
        autoScrollRunnable?.let { autoScrollHandler.removeCallbacks(it) }
    }

    override fun onPause() {
        super.onPause()
        clearCarouselTimer()
        (activity as? MainActivity)?.showMainUI()
    }

    override fun onResume() {
        super.onResume()
        if (!binding.shimmerContainer.isVisible) {
            binding.stadiumImagePager.adapter?.itemCount?.let {
                startAutoScroll(it)
            }
        }
        (activity as? MainActivity)?.hideMainUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearCarouselTimer()
        contentRevealHandler.removeCallbacksAndMessages(null)
        binding.stadiumImagePager.adapter = null
        _binding = null
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
    }
}
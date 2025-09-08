package uz.umarov.fcneftchi.ui.home.adapter

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import coil.load
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.data.model.Video
import uz.umarov.fcneftchi.databinding.ItemHomeHeaderBinding
import uz.umarov.fcneftchi.databinding.ItemHomeHeroCarouselBinding
import uz.umarov.fcneftchi.databinding.ItemHomeLastResultBinding
import uz.umarov.fcneftchi.databinding.ItemHomeNewsCarouselBinding
import uz.umarov.fcneftchi.databinding.ItemHomeNextMatchBinding
import uz.umarov.fcneftchi.databinding.ItemHomeStandingsBinding
import uz.umarov.fcneftchi.databinding.ItemVideoBinding
import uz.umarov.fcneftchi.ui.home.HomeListItem
import uz.umarov.fcneftchi.util.DateUtils
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

private const val VIEW_TYPE_HERO_CAROUSEL = 0
private const val VIEW_TYPE_NEXT_MATCH = 1
private const val VIEW_TYPE_LAST_RESULT = 2
private const val VIEW_TYPE_FEATURED_VIDEO = 3
private const val VIEW_TYPE_HEADER = 4
private const val VIEW_TYPE_NEWS_CAROUSEL = 5
private const val VIEW_TYPE_STANDINGS = 6

class HomeAdapter(
    private val onNavigate: (Int) -> Unit,
    private val onArticleClick: (NewsArticle) -> Unit,
    private val onMatchClick: (Match) -> Unit,
    private val onVideoClick: (Video) -> Unit
) : ListAdapter<HomeListItem, RecyclerView.ViewHolder>(HomeDiffCallback) {

    private val countdownHandlers = mutableMapOf<Int, Handler>()
    private val heroCarouselHandlers = mutableMapOf<Int, Handler>()


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HomeListItem.HeroCarouselItem -> VIEW_TYPE_HERO_CAROUSEL
            is HomeListItem.NextMatchItem -> VIEW_TYPE_NEXT_MATCH
            is HomeListItem.LastResultItem -> VIEW_TYPE_LAST_RESULT
            is HomeListItem.FeaturedVideoItem -> VIEW_TYPE_FEATURED_VIDEO
            is HomeListItem.HeaderItem -> VIEW_TYPE_HEADER
            is HomeListItem.NewsCarouselItem -> VIEW_TYPE_NEWS_CAROUSEL
            is HomeListItem.StandingsItem -> VIEW_TYPE_STANDINGS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HERO_CAROUSEL -> HeroCarouselViewHolder(
                ItemHomeHeroCarouselBinding.inflate(
                    inflater,
                    parent,
                    false
                ), onArticleClick
            )

            VIEW_TYPE_NEXT_MATCH -> NextMatchViewHolder(
                ItemHomeNextMatchBinding.inflate(
                    inflater,
                    parent,
                    false
                ), onMatchClick
            )

            VIEW_TYPE_LAST_RESULT -> LastResultViewHolder(
                ItemHomeLastResultBinding.inflate(
                    inflater,
                    parent,
                    false
                ), onMatchClick
            )

            VIEW_TYPE_FEATURED_VIDEO -> FeaturedVideoViewHolder(
                ItemVideoBinding.inflate(
                    inflater,
                    parent,
                    false
                ), onVideoClick
            )

            VIEW_TYPE_HEADER -> HeaderViewHolder(
                ItemHomeHeaderBinding.inflate(
                    inflater,
                    parent,
                    false
                ), onNavigate
            )

            VIEW_TYPE_NEWS_CAROUSEL -> NewsCarouselViewHolder(
                ItemHomeNewsCarouselBinding.inflate(
                    inflater,
                    parent,
                    false
                ), onArticleClick
            )

            VIEW_TYPE_STANDINGS -> StandingsViewHolder(
                ItemHomeStandingsBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HomeListItem.HeroCarouselItem -> (holder as HeroCarouselViewHolder).bind(
                item.articles,
                heroCarouselHandlers.getOrPut(position) { Handler(Looper.getMainLooper()) }) // Pass handler
            is HomeListItem.NextMatchItem -> (holder as NextMatchViewHolder).bind(item.match)
            is HomeListItem.LastResultItem -> (holder as LastResultViewHolder).bind(item.match)
            is HomeListItem.FeaturedVideoItem -> (holder as FeaturedVideoViewHolder).bind(item.video)
            is HomeListItem.HeaderItem -> (holder as HeaderViewHolder).bind(item)
            is HomeListItem.NewsCarouselItem -> (holder as NewsCarouselViewHolder).bind(item.articles)
            is HomeListItem.StandingsItem -> (holder as StandingsViewHolder).bind(item.standings)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is NextMatchViewHolder) holder.clearCountdown()
        if (holder is HeroCarouselViewHolder) holder.clearCarouselTimer()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        release()
    }

    fun release() {
        countdownHandlers.values.forEach { it.removeCallbacksAndMessages(null) }
        countdownHandlers.clear()
    }

    class HeroCarouselViewHolder(
        private val binding: ItemHomeHeroCarouselBinding,
        private val onArticleClick: (NewsArticle) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var autoScrollHandler: Handler
        private var autoScrollRunnable: Runnable? = null
        private val AUTO_SCROLL_DELAY = 5000L

        fun bind(articles: List<NewsArticle>, handler: Handler) {
            autoScrollHandler = handler

            val pagerAdapter = HeroNewsPagerAdapter(onArticleClick)
            binding.heroNewsViewPager.adapter = pagerAdapter
            pagerAdapter.submitList(articles)

            val indicatorAdapter = IndicatorAdapter(articles.size)
            binding.heroNewsIndicator.adapter = indicatorAdapter

            binding.heroNewsViewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    indicatorAdapter.selectedPosition = position
                    indicatorAdapter.notifyDataSetChanged()
                }
            })

            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer(MarginPageTransformer(40))
            compositePageTransformer.addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = 0.85f + r * 0.15f
            }
            binding.heroNewsViewPager.setPageTransformer(compositePageTransformer)

            startAutoScroll(articles.size)
        }

        private fun startAutoScroll(itemCount: Int) {
            clearCarouselTimer()
            if (itemCount <= 1) return

            autoScrollRunnable = object : Runnable {
                override fun run() {
                    val currentItem = binding.heroNewsViewPager.currentItem
                    val nextItem = (currentItem + 1) % itemCount
                    binding.heroNewsViewPager.setCurrentItem(nextItem, true)
                    autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY)
                }
            }
            autoScrollHandler.postDelayed(autoScrollRunnable!!, AUTO_SCROLL_DELAY)
        }

        fun clearCarouselTimer() {
            autoScrollRunnable?.let { autoScrollHandler.removeCallbacks(it) }
        }
    }

    class NextMatchViewHolder(
        private val binding: ItemHomeNextMatchBinding,
        private val onMatchClick: (Match) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private val countdownHandler = Handler(Looper.getMainLooper())
        private var countdownRunnable: Runnable? = null

        fun bind(match: Match) {
            binding.root.setOnClickListener { onMatchClick(match) }
            binding.homeTeamLogo.load(match.homeTeam.logoUrl)
            binding.awayTeamLogo.load(match.awayTeam.logoUrl)
            binding.homeTeamName.text = match.homeTeam.name
            binding.awayTeamName.text = match.awayTeam.name
            binding.matchDate.text = formatHomeMatchDate(match.matchDate)
            binding.matchCompetition.text = match.competition
            startCountdown(match.matchDate)
        }

        fun clearCountdown() {
            countdownRunnable?.let { countdownHandler.removeCallbacks(it) }
        }

        private fun startCountdown(matchDateString: String?) {
            clearCountdown()
            if (matchDateString == null) return

            val matchDate = DateUtils.parseDate(matchDateString) ?: return

            countdownRunnable = object : Runnable {
                @SuppressLint("DefaultLocale")
                override fun run() {
                    val diff = matchDate.time - System.currentTimeMillis()
                    if (diff > 0) {
                        val days = diff / (1000 * 60 * 60 * 24)
                        val hours = (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
                        val minutes = (diff % (1000 * 60 * 60)) / (1000 * 60)
                        val seconds = (diff % (1000 * 60)) / 1000
                        binding.countdownTimer.text = String.format(
                            "%02d : %02d : %02d : %02d",
                            days,
                            hours,
                            minutes,
                            seconds
                        )
                        countdownHandler.postDelayed(this, 1000)
                    } else {
                        binding.countdownTimer.text = "STARTED"
                    }
                }
            }
            countdownHandler.post(countdownRunnable!!)
        }

        private fun formatHomeMatchDate(dateString: String?): String {
            val date = DateUtils.parseDate(dateString) ?: return "N/A"
            val formatter = SimpleDateFormat("dd MMM, HH:mm", Locale.ENGLISH)
            return formatter.format(date).uppercase(Locale.ROOT)
        }
    }

    class LastResultViewHolder(
        private val binding: ItemHomeLastResultBinding,
        private val onMatchClick: (Match) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(match: Match) {
            binding.root.setOnClickListener { onMatchClick(match) }

            binding.homeTeamName.text = match.homeTeam.name
            binding.awayTeamName.text = match.awayTeam.name
            binding.homeTeamLogo.load(match.homeTeam.logoUrl)
            binding.awayTeamLogo.load(match.awayTeam.logoUrl)
            binding.score.text = "${match.homeScore} - ${match.awayScore}"
            binding.matchCompetition.text = match.competition

            val date = DateUtils.parseDate(match.matchDate)
            binding.matchDate.text = if (date != null) {
                SimpleDateFormat("dd MMM, HH:mm", Locale.ENGLISH).format(date)
                    .toUpperCase(Locale.ROOT)
            } else {
                "N/A"
            }
        }
    }


    class HeaderViewHolder(
        private val binding: ItemHomeHeaderBinding,
        private val onNavigate: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeListItem.HeaderItem) {
            binding.headerTitle.text = item.title
            binding.viewAllButton.setOnClickListener { onNavigate(item.destinationId) }
        }
    }

    class NewsCarouselViewHolder(
        private val binding: ItemHomeNewsCarouselBinding,
        private val onArticleClick: (NewsArticle) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(articles: List<NewsArticle>) {
            val newsHomeAdapter = NewsHomeAdapter(onArticleClick)
            binding.newsCarouselRecyclerView.adapter = newsHomeAdapter
            newsHomeAdapter.submitList(articles)
        }
    }

    class StandingsViewHolder(private val binding: ItemHomeStandingsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(standings: List<LeagueStanding>) {
            val standingsHomeAdapter = StandingsHomeAdapter()
            binding.standingsRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            binding.standingsRecyclerView.adapter = standingsHomeAdapter
            standingsHomeAdapter.submitList(standings)
        }
    }

    class FeaturedVideoViewHolder(
        private val binding: ItemVideoBinding,
        private val onVideoClick: (Video) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(video: Video) {
            binding.root.setOnClickListener { onVideoClick(video) }
            binding.videoThumbnail.load(video.thumbnailUrl)
            binding.videoTitle.text = video.title
            binding.videoDuration.text = video.duration
        }
    }

    object HomeDiffCallback : DiffUtil.ItemCallback<HomeListItem>() {
        override fun areItemsTheSame(oldItem: HomeListItem, newItem: HomeListItem): Boolean {
            return oldItem.javaClass == newItem.javaClass
        }

        override fun areContentsTheSame(oldItem: HomeListItem, newItem: HomeListItem): Boolean {
            return oldItem == newItem
        }
    }
}
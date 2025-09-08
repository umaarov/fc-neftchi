package uz.umarov.fcneftchi.ui.home.adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.databinding.*
import uz.umarov.fcneftchi.ui.home.HomeListItem
import uz.umarov.fcneftchi.util.DateUtils
import java.text.SimpleDateFormat
import java.util.*

private const val VIEW_TYPE_NEXT_MATCH = 0
private const val VIEW_TYPE_LAST_RESULT = 1
private const val VIEW_TYPE_HEADER = 2
private const val VIEW_TYPE_NEWS_CAROUSEL = 3
private const val VIEW_TYPE_STANDINGS = 4

class HomeAdapter(
    private val onNavigate: (Int) -> Unit,
    private val onArticleClick: (NewsArticle) -> Unit,
    private val onMatchClick: (Match) -> Unit
) : ListAdapter<HomeListItem, RecyclerView.ViewHolder>(HomeDiffCallback) {

    private val countdownHandlers = mutableMapOf<Int, Handler>()

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HomeListItem.NextMatchItem -> VIEW_TYPE_NEXT_MATCH
            is HomeListItem.LastResultItem -> VIEW_TYPE_LAST_RESULT
            is HomeListItem.HeaderItem -> VIEW_TYPE_HEADER
            is HomeListItem.NewsCarouselItem -> VIEW_TYPE_NEWS_CAROUSEL
            is HomeListItem.StandingsItem -> VIEW_TYPE_STANDINGS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_NEXT_MATCH -> NextMatchViewHolder(ItemHomeNextMatchBinding.inflate(inflater, parent, false), onMatchClick)
            VIEW_TYPE_LAST_RESULT -> LastResultViewHolder(ItemHomeLastResultBinding.inflate(inflater, parent, false), onMatchClick)
            VIEW_TYPE_HEADER -> HeaderViewHolder(ItemHomeHeaderBinding.inflate(inflater, parent, false), onNavigate)
            VIEW_TYPE_NEWS_CAROUSEL -> NewsCarouselViewHolder(ItemHomeNewsCarouselBinding.inflate(inflater, parent, false), onArticleClick)
            VIEW_TYPE_STANDINGS -> StandingsViewHolder(ItemHomeStandingsBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HomeListItem.NextMatchItem -> (holder as NextMatchViewHolder).bind(item.match)
            is HomeListItem.LastResultItem -> (holder as LastResultViewHolder).bind(item.match)
            is HomeListItem.HeaderItem -> (holder as HeaderViewHolder).bind(item)
            is HomeListItem.NewsCarouselItem -> (holder as NewsCarouselViewHolder).bind(item.articles)
            is HomeListItem.StandingsItem -> (holder as StandingsViewHolder).bind(item.standings)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is NextMatchViewHolder) {
            holder.clearCountdown()
        }
    }

    fun release() {
        countdownHandlers.values.forEach { it.removeCallbacksAndMessages(null) }
        countdownHandlers.clear()
    }

    class NextMatchViewHolder(private val binding: ItemHomeNextMatchBinding, private val onMatchClick: (Match) -> Unit) : RecyclerView.ViewHolder(binding.root) {
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
                override fun run() {
                    val diff = matchDate.time - System.currentTimeMillis()
                    if (diff > 0) {
                        val days = diff / (1000 * 60 * 60 * 24)
                        val hours = (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
                        val minutes = (diff % (1000 * 60 * 60)) / (1000 * 60)
                        val seconds = (diff % (1000 * 60)) / 1000
                        binding.countdownTimer.text = String.format("%02d : %02d : %02d : %02d", days, hours, minutes, seconds)
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

    class LastResultViewHolder(private val binding: ItemHomeLastResultBinding, private val onMatchClick: (Match) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(match: Match) {
            binding.root.setOnClickListener { onMatchClick(match) }
            val resultBinding = binding.resultInclude
            resultBinding.homeTeamName.text = match.homeTeam.name
            resultBinding.awayTeamName.text = match.awayTeam.name
            resultBinding.homeTeamLogo.load(match.homeTeam.logoUrl)
            resultBinding.awayTeamLogo.load(match.awayTeam.logoUrl)
            resultBinding.matchCompetition.text = match.competition
            val date = DateUtils.parseDate(match.matchDate)
            resultBinding.matchDate.text = if (date != null) SimpleDateFormat("E d MMM yyyy", Locale.getDefault()).format(date).uppercase() else ""
            resultBinding.homeScoreText.text = match.homeScore.toString()
            resultBinding.awayScoreText.text = match.awayScore.toString()

            val myTeamName = "Neftchi"
            val homeScore = match.homeScore ?: -1
            val awayScore = match.awayScore ?: -1

            val backgroundColor = when {
                match.homeTeam.name == myTeamName && homeScore > awayScore -> R.color.result_win
                match.awayTeam.name == myTeamName && awayScore > homeScore -> R.color.result_win
                homeScore == awayScore -> R.color.result_draw
                else -> R.color.result_loss
            }
            resultBinding.scoreContainer.background = ContextCompat.getDrawable(itemView.context, backgroundColor)
        }
    }

    class HeaderViewHolder(private val binding: ItemHomeHeaderBinding, private val onNavigate: (Int) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeListItem.HeaderItem) {
            binding.headerTitle.text = item.title
            binding.viewAllButton.setOnClickListener { onNavigate(item.destinationId) }
        }
    }

    class NewsCarouselViewHolder(private val binding: ItemHomeNewsCarouselBinding, private val onArticleClick: (NewsArticle) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(articles: List<NewsArticle>) {
            val newsHomeAdapter = NewsHomeAdapter(onArticleClick)
            binding.newsCarouselRecyclerView.adapter = newsHomeAdapter
            newsHomeAdapter.submitList(articles)
        }
    }

    class StandingsViewHolder(private val binding: ItemHomeStandingsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(standings: List<LeagueStanding>) {
            val standingsHomeAdapter = StandingsHomeAdapter()
            binding.standingsRecyclerView.adapter = standingsHomeAdapter
            standingsHomeAdapter.submitList(standings)
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
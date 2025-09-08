package uz.umarov.fcneftchi.ui.matches.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.databinding.ItemMonthHeaderBinding
import uz.umarov.fcneftchi.databinding.ItemResultBinding
import uz.umarov.fcneftchi.ui.matches.ResultListItem
import uz.umarov.fcneftchi.util.DateUtils
import java.text.SimpleDateFormat
import java.util.Locale

private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_RESULT = 1

class ResultAdapter(
    private val onItemClick: (Match) -> Unit
) : ListAdapter<ResultListItem, RecyclerView.ViewHolder>(ResultDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ResultListItem.HeaderItem -> VIEW_TYPE_HEADER
            is ResultListItem.ResultItem -> VIEW_TYPE_RESULT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            VIEW_TYPE_RESULT -> ResultViewHolder.from(parent, onItemClick)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ResultViewHolder -> {
                val resultItem = getItem(position) as ResultListItem.ResultItem
                holder.bind(resultItem.match)
            }

            is HeaderViewHolder -> {
                val headerItem = getItem(position) as ResultListItem.HeaderItem
                holder.bind(headerItem.monthYear)
            }
        }
    }

    class ResultViewHolder(
        private val binding: ItemResultBinding,
        private val onItemClick: (Match) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormatter = SimpleDateFormat("E d MMM yyyy", Locale.getDefault())

        fun bind(match: Match) {
            binding.root.setOnClickListener {
                onItemClick(match)
            }
            val context = binding.root.context

            binding.homeTeamName.text = match.homeTeam.name
            binding.awayTeamName.text = match.awayTeam.name
            binding.homeScoreText.text = match.homeScore.toString()
            binding.awayScoreText.text = match.awayScore.toString()
            binding.matchCompetition.text = match.competition.uppercase()

            val date = DateUtils.parseDate(match.matchDate)
            binding.matchDate.text =
                if (date != null) dateFormatter.format(date).uppercase() else "DATE UNAVAILABLE"

            binding.homeTeamLogo.load(match.homeTeam.logoUrl) { crossfade(true) }
            binding.awayTeamLogo.load(match.awayTeam.logoUrl) { crossfade(true) }

            val homeScore = match.homeScore ?: -1
            val awayScore = match.awayScore ?: -1

            val myTeamName = "Neftchi"

            val backgroundColor = when {
                homeScore == -1 || awayScore == -1 -> R.color.result_draw
                match.homeTeam.name == myTeamName && homeScore > awayScore -> R.color.result_win
                match.awayTeam.name == myTeamName && awayScore > homeScore -> R.color.result_win
                homeScore == awayScore -> R.color.result_draw
                else -> R.color.result_loss
            }
            binding.scoreContainer.background = ContextCompat.getDrawable(context, backgroundColor)
        }

        companion object {
            fun from(parent: ViewGroup, onItemClick: (Match) -> Unit): ResultViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemResultBinding.inflate(layoutInflater, parent, false)
                return ResultViewHolder(binding, onItemClick)
            }
        }
    }

    class HeaderViewHolder(private val binding: ItemMonthHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(monthYear: String) {
            binding.monthHeaderText.text = monthYear
        }

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemMonthHeaderBinding.inflate(layoutInflater, parent, false)
                return HeaderViewHolder(binding)
            }
        }
    }
}

class ResultDiffCallback : DiffUtil.ItemCallback<ResultListItem>() {
    override fun areItemsTheSame(oldItem: ResultListItem, newItem: ResultListItem): Boolean {
        return (oldItem is ResultListItem.ResultItem && newItem is ResultListItem.ResultItem && oldItem.match.id == newItem.match.id) ||
                (oldItem is ResultListItem.HeaderItem && newItem is ResultListItem.HeaderItem && oldItem.monthYear == newItem.monthYear)
    }

    override fun areContentsTheSame(oldItem: ResultListItem, newItem: ResultListItem): Boolean {
        return oldItem == newItem
    }
}
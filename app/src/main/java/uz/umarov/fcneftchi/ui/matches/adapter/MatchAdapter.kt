package uz.umarov.fcneftchi.ui.matches.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load // <-- Import Coil
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.databinding.ItemMatchBinding
import uz.umarov.fcneftchi.databinding.ItemMonthHeaderBinding
import uz.umarov.fcneftchi.ui.matches.FixtureListItem
import uz.umarov.fcneftchi.util.DateUtils
import java.text.SimpleDateFormat
import java.util.Locale

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class MatchAdapter(private val onItemClick: (Match) -> Unit) :
    ListAdapter<FixtureListItem, RecyclerView.ViewHolder>(MatchDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FixtureListItem.HeaderItem -> ITEM_VIEW_TYPE_HEADER
            is FixtureListItem.MatchItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> MatchViewHolder.from(parent, onItemClick)
            else -> throw IllegalArgumentException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MatchViewHolder -> {
                val matchItem = getItem(position) as FixtureListItem.MatchItem
                holder.bind(matchItem.match)
            }

            is HeaderViewHolder -> {
                val headerItem = getItem(position) as FixtureListItem.HeaderItem
                holder.bind(headerItem)
            }
        }
    }

    class MatchViewHolder(
        private val binding: ItemMatchBinding,
        private val onItemClick: (Match) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormatter = SimpleDateFormat("E d MMM yyyy", Locale.getDefault())
        private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun bind(match: Match) {
            binding.root.setOnClickListener {
                onItemClick(match)
            }
            binding.homeTeamName.text = match.homeTeam.name
            binding.awayTeamName.text = match.awayTeam.name
            binding.matchCompetition.text = match.competition.uppercase()

            val date = DateUtils.parseDate(match.matchDate)

            if (date != null) {
                binding.matchDate.text = dateFormatter.format(date).uppercase()
                binding.timeBackground.text = timeFormatter.format(date)
            } else {
                binding.matchDate.text = "DATE UNAVAILABLE"
                binding.timeBackground.text = "N/A"
            }

            binding.homeTeamLogo.load(match.homeTeam.logoUrl) { crossfade(true) }
            binding.awayTeamLogo.load(match.awayTeam.logoUrl) { crossfade(true) }
        }

        companion object {
            fun from(parent: ViewGroup, onItemClick: (Match) -> Unit): MatchViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemMatchBinding.inflate(inflater, parent, false)
                return MatchViewHolder(binding, onItemClick)
            }
        }
    }

    class HeaderViewHolder(private val binding: ItemMonthHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: FixtureListItem.HeaderItem) {
            binding.monthHeaderText.text = header.monthYear
        }

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemMonthHeaderBinding.inflate(inflater, parent, false)
                return HeaderViewHolder(binding)
            }
        }
    }
}

class MatchDiffCallback : DiffUtil.ItemCallback<FixtureListItem>() {
    override fun areItemsTheSame(oldItem: FixtureListItem, newItem: FixtureListItem): Boolean {
        return (oldItem is FixtureListItem.MatchItem && newItem is FixtureListItem.MatchItem && oldItem.match.id == newItem.match.id) ||
                (oldItem is FixtureListItem.HeaderItem && newItem is FixtureListItem.HeaderItem && oldItem.monthYear == newItem.monthYear)
    }

    override fun areContentsTheSame(oldItem: FixtureListItem, newItem: FixtureListItem): Boolean {
        return oldItem == newItem
    }
}
package uz.umarov.fcneftchi.ui.matches.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load // <-- Import Coil
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.databinding.ItemMatchBinding
import uz.umarov.fcneftchi.databinding.ItemMonthHeaderBinding
import uz.umarov.fcneftchi.ui.matches.FixtureListItem
import uz.umarov.fcneftchi.util.DateFormatter
import uz.umarov.fcneftchi.util.DateUtils

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

        fun bind(match: Match) {
            val context = binding.root.context
            binding.root.setOnClickListener {
                onItemClick(match)
            }
            binding.homeTeamName.text = match.homeTeam.name
            binding.awayTeamName.text = match.awayTeam.name
            binding.matchCompetition.text = competitionLabel(match, context).uppercase()

            val date = DateUtils.parseDate(match.matchDate)

            if (date != null) {
                binding.matchDate.text = DateFormatter.formatMatchListDate(date).uppercase()
                binding.timeBackground.text = DateFormatter.formatMatchListTime(date)
            } else {
                binding.matchDate.text = context.getString(R.string.date_unavailable)
                binding.timeBackground.text = context.getString(R.string.value_unavailable)
            }

            binding.homeTeamLogo.load(match.homeTeam.logoUrl) { crossfade(true) }
            binding.awayTeamLogo.load(match.awayTeam.logoUrl) { crossfade(true) }
        }

        private fun competitionLabel(match: Match, context: android.content.Context): String =
            match.competition.takeIf { it.isNotBlank() }
                ?: context.getString(R.string.competition_superliga)

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
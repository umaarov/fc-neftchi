package uz.umarov.fcneftchi.ui.matches.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.umarov.fcneftchi.data.model.Match
import uz.umarov.fcneftchi.data.model.MatchStatus
import uz.umarov.fcneftchi.databinding.ItemMatchBinding

class MatchAdapter : ListAdapter<Match, MatchAdapter.MatchViewHolder>(MatchDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MatchViewHolder(private val binding: ItemMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(match: Match) {
            binding.homeTeamLogo.load(match.homeTeam.logoUrl)
            binding.awayTeamLogo.load(match.awayTeam.logoUrl)
            binding.homeTeamName.text = match.homeTeam.name
            binding.awayTeamName.text = match.awayTeam.name
            binding.matchCompetition.text = match.competition

            if (match.status == MatchStatus.FINISHED) {
                binding.scoreGroup.isVisible = true
                binding.matchDate.isVisible = false
                binding.homeTeamScore.text = match.homeScore.toString()
                binding.awayTeamScore.text = match.awayScore.toString()
            } else {
                binding.scoreGroup.isVisible = false
                binding.matchDate.isVisible = true
                binding.matchDate.text = "28 JUL\n19:00"
            }
        }
    }

    object MatchDiffCallback : DiffUtil.ItemCallback<Match>() {
        override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem == newItem
        }
    }
}
package uz.umarov.fcneftchi.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.databinding.ItemStandingBinding

class StandingsAdapter : ListAdapter<LeagueStanding, StandingsAdapter.StandingViewHolder>(StandingDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandingViewHolder {
        val binding = ItemStandingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StandingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StandingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StandingViewHolder(private val binding: ItemStandingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(standing: LeagueStanding) {
            binding.teamPosition.text = standing.position.toString()
            binding.teamLogo.load(standing.team.logoUrl)
            binding.teamName.text = standing.team.name
            binding.teamPlayed.text = standing.played.toString()
            binding.teamPoints.text = standing.points.toString()
        }
    }

    object StandingDiffCallback : DiffUtil.ItemCallback<LeagueStanding>() {
        override fun areItemsTheSame(oldItem: LeagueStanding, newItem: LeagueStanding): Boolean {
            return oldItem.team.id == newItem.team.id
        }

        override fun areContentsTheSame(oldItem: LeagueStanding, newItem: LeagueStanding): Boolean {
            return oldItem == newItem
        }
    }
}
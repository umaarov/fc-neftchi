package uz.umarov.fcneftchi.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.databinding.ItemStandingsHomeRowBinding

class StandingsHomeAdapter : ListAdapter<LeagueStanding, StandingsHomeAdapter.StandingsHomeViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandingsHomeViewHolder {
        val binding = ItemStandingsHomeRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StandingsHomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StandingsHomeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class StandingsHomeViewHolder(private val binding: ItemStandingsHomeRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(standing: LeagueStanding) {
            binding.teamPosition.text = standing.position.toString()
            binding.teamLogo.load(standing.team.logoUrl)
            binding.teamName.text = standing.team.name
            binding.statPoints.text = standing.points.toString()
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<LeagueStanding>() {
        override fun areItemsTheSame(oldItem: LeagueStanding, newItem: LeagueStanding): Boolean = oldItem.team.id == newItem.team.id
        override fun areContentsTheSame(oldItem: LeagueStanding, newItem: LeagueStanding): Boolean = oldItem == newItem
    }
}
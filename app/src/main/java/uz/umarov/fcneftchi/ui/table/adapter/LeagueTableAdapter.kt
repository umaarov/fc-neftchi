package uz.umarov.fcneftchi.ui.table.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.LeagueStanding
import uz.umarov.fcneftchi.databinding.ItemLeagueTableBinding

class LeagueTableAdapter : ListAdapter<LeagueStanding, LeagueTableAdapter.TableViewHolder>(TableDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val binding = ItemLeagueTableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TableViewHolder(private val binding: ItemLeagueTableBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(standing: LeagueStanding) {
            binding.teamPosition.text = standing.position.toString()
            binding.teamLogo.load(standing.team.logoUrl) {
                placeholder(R.drawable.ic_team)
                error(R.drawable.ic_team)
            }
            binding.teamName.text = standing.team.name
            binding.statGames.text = standing.played.toString()
            binding.statWins.text = standing.wins.toString()
            binding.statDraws.text = standing.draws.toString()
            binding.statLosses.text = standing.losses.toString()
            binding.statPoints.text = standing.points.toString()
        }
    }

    object TableDiffCallback : DiffUtil.ItemCallback<LeagueStanding>() {
        override fun areItemsTheSame(oldItem: LeagueStanding, newItem: LeagueStanding): Boolean = oldItem.team.id == newItem.team.id
        override fun areContentsTheSame(oldItem: LeagueStanding, newItem: LeagueStanding): Boolean = oldItem == newItem
    }
}
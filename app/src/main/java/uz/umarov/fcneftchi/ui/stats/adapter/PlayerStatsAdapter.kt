package uz.umarov.fcneftchi.ui.stats.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.umarov.fcneftchi.data.model.PlayerStatistics
import uz.umarov.fcneftchi.databinding.ItemPlayerStatsBinding

class PlayerStatsAdapter : ListAdapter<PlayerStatistics, PlayerStatsAdapter.PlayerStatsViewHolder>(PlayerStatsDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerStatsViewHolder {
        val binding = ItemPlayerStatsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerStatsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerStatsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PlayerStatsViewHolder(private val binding: ItemPlayerStatsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(stats: PlayerStatistics) {
            binding.playerImage.load(stats.photo) {
                placeholder(uz.umarov.fcneftchi.R.drawable.ic_team)
                error(uz.umarov.fcneftchi.R.drawable.ic_team)
            }
            binding.playerName.text = "${stats.firstName ?: ""} ${stats.lastName}".trim()
            binding.playerNumber.text = stats.number?.toString() ?: ""
            binding.statGames.text = stats.games.toString()
            binding.statGoals.text = stats.goals.toString()
            binding.statAssists.text = stats.assists.toString()
            binding.statYellow.text = stats.yellowCards.toString()
            binding.statRed.text = stats.redCards.toString()
        }
    }

    object PlayerStatsDiffCallback : DiffUtil.ItemCallback<PlayerStatistics>() {
        override fun areItemsTheSame(oldItem: PlayerStatistics, newItem: PlayerStatistics): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PlayerStatistics, newItem: PlayerStatistics): Boolean = oldItem == newItem
    }
}
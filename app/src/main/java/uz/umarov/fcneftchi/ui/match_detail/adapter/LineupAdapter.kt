package uz.umarov.fcneftchi.ui.match_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.umarov.fcneftchi.data.model.GameLineupPlayer
import uz.umarov.fcneftchi.databinding.ItemLineupPlayerBinding

class LineupAdapter : ListAdapter<GameLineupPlayer, LineupAdapter.LineupViewHolder>(LineupDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineupViewHolder {
        val binding = ItemLineupPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LineupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LineupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LineupViewHolder(private val binding: ItemLineupPlayerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(lineupPlayer: GameLineupPlayer) {
            binding.playerNumber.text = lineupPlayer.player.clubPlayers.firstOrNull()?.number?.toString() ?: ""
            binding.playerName.text = "${lineupPlayer.player.firstName ?: ""} ${lineupPlayer.player.lastName}".trim()
        }
    }

    object LineupDiffCallback : DiffUtil.ItemCallback<GameLineupPlayer>() {
        override fun areItemsTheSame(oldItem: GameLineupPlayer, newItem: GameLineupPlayer): Boolean = oldItem.player.id == newItem.player.id
        override fun areContentsTheSame(oldItem: GameLineupPlayer, newItem: GameLineupPlayer): Boolean = oldItem == newItem
    }
}
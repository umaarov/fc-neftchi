package uz.umarov.fcneftchi.ui.topplayers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.TopPlayer
import uz.umarov.fcneftchi.databinding.ItemTopPlayerBinding

class TopPlayerAdapter(private val statType: StatType) :
    ListAdapter<TopPlayer, TopPlayerAdapter.TopPlayerViewHolder>(TopPlayerDiffCallback) {

    enum class StatType { GOALS, ASSISTS }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopPlayerViewHolder {
        val binding =
            ItemTopPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopPlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopPlayerViewHolder, position: Int) {
        holder.bind(getItem(position), position + 1, statType)
    }

    class TopPlayerViewHolder(private val binding: ItemTopPlayerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(player: TopPlayer, position: Int, statType: StatType) {
            binding.playerPosition.text = position.toString()
            binding.playerImage.load(player.photo) {
                placeholder(R.drawable.ic_team)
                error(R.drawable.ic_team)
            }
            binding.playerName.text = "${player.firstName ?: ""} ${player.lastName}".trim()
            binding.clubName.text = player.clubTitle
            binding.statValue.text =
                if (statType == StatType.GOALS) player.goals.toString() else player.assists.toString()
        }
    }

    object TopPlayerDiffCallback : DiffUtil.ItemCallback<TopPlayer>() {
        override fun areItemsTheSame(oldItem: TopPlayer, newItem: TopPlayer): Boolean =
            oldItem.playerId == newItem.playerId

        override fun areContentsTheSame(oldItem: TopPlayer, newItem: TopPlayer): Boolean =
            oldItem == newItem
    }
}
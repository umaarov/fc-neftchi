package uz.umarov.fcneftchi.ui.topplayers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.umarov.fcneftchi.databinding.ItemTopPlayerBinding
import uz.umarov.fcneftchi.databinding.ItemTopPlayerHeaderBinding
import uz.umarov.fcneftchi.ui.topplayers.TopPlayerListItem

private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_PLAYER = 1

class TopPlayerAdapter :
    ListAdapter<TopPlayerListItem, RecyclerView.ViewHolder>(TopPlayerDiffCallback) {

    enum class StatType { GOALS, ASSISTS }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TopPlayerListItem.HeaderItem -> VIEW_TYPE_HEADER
            is TopPlayerListItem.PlayerItem -> VIEW_TYPE_PLAYER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            VIEW_TYPE_PLAYER -> PlayerViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is TopPlayerListItem.HeaderItem -> (holder as HeaderViewHolder).bind(item)
            is TopPlayerListItem.PlayerItem -> (holder as PlayerViewHolder).bind(item, position)
        }
    }

    class PlayerViewHolder(private val binding: ItemTopPlayerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(playerItem: TopPlayerListItem.PlayerItem, position: Int) {
            val player = playerItem.player
            binding.playerPosition.text = position.toString()
            binding.playerImage.load(player.photo) {
                crossfade(true)
            }
            binding.playerName.text = "${player.firstName ?: ""} ${player.lastName}".trim()
            binding.clubName.text = player.clubTitle
            binding.statValue.text = if (playerItem.statType == StatType.GOALS) {
                player.goals.toString()
            } else {
                player.assists.toString()
            }
        }

        companion object {
            fun from(parent: ViewGroup): PlayerViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemTopPlayerBinding.inflate(inflater, parent, false)
                return PlayerViewHolder(binding)
            }
        }
    }

    class HeaderViewHolder(private val binding: ItemTopPlayerHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(headerItem: TopPlayerListItem.HeaderItem) {
            binding.headerTitle.setText(headerItem.titleRes)
        }

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemTopPlayerHeaderBinding.inflate(inflater, parent, false)
                return HeaderViewHolder(binding)
            }
        }
    }

    object TopPlayerDiffCallback : DiffUtil.ItemCallback<TopPlayerListItem>() {
        override fun areItemsTheSame(
            oldItem: TopPlayerListItem,
            newItem: TopPlayerListItem
        ): Boolean {
            return (oldItem is TopPlayerListItem.PlayerItem && newItem is TopPlayerListItem.PlayerItem && oldItem.player.playerId == newItem.player.playerId) ||
                    (oldItem is TopPlayerListItem.HeaderItem && newItem is TopPlayerListItem.HeaderItem && oldItem.titleRes == newItem.titleRes)
        }

        override fun areContentsTheSame(
            oldItem: TopPlayerListItem,
            newItem: TopPlayerListItem
        ): Boolean = oldItem == newItem
    }
}
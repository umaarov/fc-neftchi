package uz.umarov.fcneftchi.ui.bookmarks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.PlayerPosition
import uz.umarov.fcneftchi.databinding.ItemBookmarkPlayerBinding

class BookmarkedPlayerAdapter(
    private val onItemClick: (Player) -> Unit,
) : ListAdapter<Player, BookmarkedPlayerAdapter.ViewHolder>(PlayerDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookmarkPlayerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemBookmarkPlayerBinding,
        private val onItemClick: (Player) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(player: Player) {
            val context = binding.root.context
            binding.playerImage.load(player.imageUrl) {
                placeholder(R.drawable.player_placeholder)
                error(R.drawable.player_placeholder)
            }
            binding.playerNumber.text = context.getString(R.string.bookmark_player_number_format, player.number)
            binding.playerName.text = player.name
            val positionRes = when (player.position) {
                PlayerPosition.GOALKEEPER -> R.string.position_goalkeeper
                PlayerPosition.DEFENDER -> R.string.position_defender
                PlayerPosition.MIDFIELDER -> R.string.position_midfielder
                PlayerPosition.ATTACKER -> R.string.position_attacker
                PlayerPosition.UNKNOWN -> R.string.position_unknown
            }
            binding.playerMeta.text = context.getString(
                R.string.bookmark_player_meta_format,
                context.getString(positionRes),
                player.nationality
            )
            binding.root.setOnClickListener { onItemClick(player) }
        }
    }

    object PlayerDiffCallback : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean =
            oldItem == newItem
    }
}

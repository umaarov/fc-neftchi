package uz.umarov.fcneftchi.ui.team.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.data.model.PlayerPosition
import uz.umarov.fcneftchi.databinding.ItemPlayerBinding
import uz.umarov.fcneftchi.databinding.ItemPlayerHeaderBinding
import uz.umarov.fcneftchi.ui.team.TeamFragmentDirections
import uz.umarov.fcneftchi.ui.team.TeamListItem
import java.util.Locale

private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_PLAYER = 1

class PlayerAdapter : ListAdapter<TeamListItem, RecyclerView.ViewHolder>(PlayerDiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TeamListItem.HeaderItem -> VIEW_TYPE_HEADER
            is TeamListItem.PlayerItem -> VIEW_TYPE_PLAYER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(
                ItemPlayerHeaderBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )

            VIEW_TYPE_PLAYER -> PlayerViewHolder(ItemPlayerBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is TeamListItem.HeaderItem -> (holder as HeaderViewHolder).bind(item)
            is TeamListItem.PlayerItem -> (holder as PlayerViewHolder).bind(item.player)
        }

    }

    class PlayerViewHolder(private val binding: ItemPlayerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player) {
            binding.root.setOnClickListener {
                val action =
                    TeamFragmentDirections.actionTeamFragmentToPlayerProfileFragment(player.id)
                it.findNavController().navigate(action)
            }
            binding.playerImage.load(player.imageUrl) { crossfade(true) }
            binding.playerNumber.text = player.number.toString()

            val names = player.name.split(" ")
            binding.playerFirstName.text = names.firstOrNull()?.uppercase() ?: ""
            binding.playerLastName.text = if (names.size > 1) names.last().uppercase() else ""
        }
    }

    class HeaderViewHolder(private val binding: ItemPlayerHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: TeamListItem.HeaderItem) {
            val context = binding.root.context
            val baseRes = when (header.position) {
                PlayerPosition.GOALKEEPER -> R.string.position_goalkeeper
                PlayerPosition.DEFENDER -> R.string.position_defender
                PlayerPosition.MIDFIELDER -> R.string.position_midfielder
                PlayerPosition.ATTACKER -> R.string.position_attacker
                PlayerPosition.UNKNOWN -> R.string.position_unknown
            }
            val title = context.getString(baseRes) +
                context.getString(R.string.position_group_suffix)
            binding.headerTitle.text = title.uppercase(Locale.ROOT)
        }
    }

    object PlayerDiffCallback : DiffUtil.ItemCallback<TeamListItem>() {
        override fun areItemsTheSame(oldItem: TeamListItem, newItem: TeamListItem): Boolean {
            return (oldItem is TeamListItem.PlayerItem && newItem is TeamListItem.PlayerItem && oldItem.player.id == newItem.player.id) ||
                    (oldItem is TeamListItem.HeaderItem && newItem is TeamListItem.HeaderItem && oldItem.position == newItem.position)
        }

        override fun areContentsTheSame(oldItem: TeamListItem, newItem: TeamListItem): Boolean =
            oldItem == newItem
    }
}
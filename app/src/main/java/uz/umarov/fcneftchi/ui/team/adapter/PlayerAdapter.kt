package uz.umarov.fcneftchi.ui.team.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.umarov.fcneftchi.data.model.Player
import uz.umarov.fcneftchi.databinding.ItemPlayerBinding
import uz.umarov.fcneftchi.ui.team.TeamFragmentDirections

// The adapter now only handles the Player type
class PlayerAdapter : ListAdapter<Player, PlayerAdapter.PlayerViewHolder>(PlayerDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PlayerViewHolder(private val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player) {
            binding.root.setOnClickListener {
                val action = TeamFragmentDirections.actionTeamFragmentToPlayerProfileFragment(player.id)
                it.findNavController().navigate(action)
            }
            binding.playerImage.load(player.imageUrl) { crossfade(true) }
            binding.playerNumber.text = player.number.toString()

            val names = player.name.split(" ")
            binding.playerFirstName.text = names.firstOrNull()?.uppercase() ?: ""
            binding.playerLastName.text = if (names.size > 1) names.last().uppercase() else ""
        }
    }

    object PlayerDiffCallback : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean = oldItem == newItem
    }
}
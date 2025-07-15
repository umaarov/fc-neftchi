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

class PlayerAdapter : ListAdapter<Player, PlayerAdapter.PlayerViewHolder>(PlayerDiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = getItem(position)
        holder.bind(player)
        holder.itemView.setOnClickListener {
            val action = TeamFragmentDirections.actionTeamFragmentToPlayerProfileFragment(player.id)
            it.findNavController().navigate(action)
        }
    }

    class PlayerViewHolder(private val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player) {
            binding.playerImage.load(player.imageUrl) {
                crossfade(true)
            }
            binding.playerName.text = player.name
            binding.playerNumber.text = player.number.toString()
        }
    }

    object PlayerDiffCallback : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean = oldItem == newItem
    }
}
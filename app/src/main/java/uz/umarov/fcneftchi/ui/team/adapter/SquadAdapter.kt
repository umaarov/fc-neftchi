package uz.umarov.fcneftchi.ui.team.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.umarov.fcneftchi.data.model.Squad
import uz.umarov.fcneftchi.databinding.ItemSquadBinding

class SquadAdapter : ListAdapter<Squad, SquadAdapter.SquadViewHolder>(SquadDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SquadViewHolder {
        val binding = ItemSquadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SquadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SquadViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SquadViewHolder(private val binding: ItemSquadBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(squad: Squad) {
            binding.squadName.text = squad.teamName
            val playerAdapter = PlayerAdapter()
            binding.playersRecyclerView.apply {
                adapter = playerAdapter
                layoutManager = GridLayoutManager(context, 3)
            }
            playerAdapter.submitList(squad.players)
        }
    }

    object SquadDiffCallback : DiffUtil.ItemCallback<Squad>() {
        override fun areItemsTheSame(oldItem: Squad, newItem: Squad): Boolean =
            oldItem.teamName == newItem.teamName

        override fun areContentsTheSame(oldItem: Squad, newItem: Squad): Boolean =
            oldItem == newItem
    }
}
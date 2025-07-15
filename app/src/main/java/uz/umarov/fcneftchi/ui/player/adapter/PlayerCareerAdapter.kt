package uz.umarov.fcneftchi.ui.player.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.PlayerCareerItem
import uz.umarov.fcneftchi.databinding.ItemPlayerCareerBinding

class PlayerCareerAdapter : ListAdapter<PlayerCareerItem, PlayerCareerAdapter.CareerViewHolder>(CareerDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CareerViewHolder {
        val binding = ItemPlayerCareerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CareerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CareerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CareerViewHolder(private val binding: ItemPlayerCareerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlayerCareerItem) {
            binding.clubLogo.load(item.clubLogo) {
                placeholder(R.drawable.ic_team)
                error(R.drawable.ic_team)
            }
            binding.clubName.text = item.clubName
            binding.seasonYear.text = item.year.toString()
            binding.gamesStat.text = item.games.toString()
            binding.goalsStat.text = item.goals.toString()
        }
    }

    object CareerDiffCallback : DiffUtil.ItemCallback<PlayerCareerItem>() {
        override fun areItemsTheSame(oldItem: PlayerCareerItem, newItem: PlayerCareerItem): Boolean = oldItem.year == newItem.year && oldItem.clubName == newItem.clubName
        override fun areContentsTheSame(oldItem: PlayerCareerItem, newItem: PlayerCareerItem): Boolean = oldItem == newItem
    }
}
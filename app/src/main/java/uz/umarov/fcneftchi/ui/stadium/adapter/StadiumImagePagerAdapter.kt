package uz.umarov.fcneftchi.ui.stadium.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.umarov.fcneftchi.databinding.ItemStadiumImageBinding

class StadiumImagePagerAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<StadiumImagePagerAdapter.StadiumImageViewHolder>() {

    inner class StadiumImageViewHolder(private val binding: ItemStadiumImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageResId: Int) {
            binding.stadiumImageView.load(imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StadiumImageViewHolder {
        val binding =
            ItemStadiumImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StadiumImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StadiumImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size
}
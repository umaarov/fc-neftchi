package uz.umarov.fcneftchi.ui.videos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.umarov.fcneftchi.data.model.Video
import uz.umarov.fcneftchi.databinding.ItemVideoBinding

class VideoAdapter(private val onItemClick: (Video) -> Unit) :
    ListAdapter<Video, VideoAdapter.VideoViewHolder>(VideoDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = getItem(position)
        holder.bind(video)
        holder.itemView.setOnClickListener { onItemClick(video) }
    }

    class VideoViewHolder(private val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(video: Video) {
            binding.videoThumbnail.load(video.thumbnailUrl)
            binding.videoTitle.text = video.title
            binding.videoDuration.text = video.duration
        }
    }

    object VideoDiffCallback : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean = oldItem == newItem
    }
}
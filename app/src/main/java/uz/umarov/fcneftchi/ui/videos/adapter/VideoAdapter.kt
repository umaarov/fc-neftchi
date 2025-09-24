package uz.umarov.fcneftchi.ui.videos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.Video
import uz.umarov.fcneftchi.databinding.ItemVideoBinding
import uz.umarov.fcneftchi.util.YouTubeUrlParser

class VideoAdapter(private val lifecycle: Lifecycle) :
    ListAdapter<Video, VideoAdapter.VideoViewHolder>(VideoDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        lifecycle.addObserver(binding.youtubePlayerView)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = getItem(position)
        holder.bind(video)
    }

    class VideoViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var youTubePlayer: YouTubePlayer? = null
        private var currentVideoId: String? = null

        fun bind(video: Video) {
            resetToThumbnail()

            binding.videoTitle.text = video.title
            binding.categoryTextView.text = "Asosiy Jamoa"
            binding.dateTextView.text = video.date
            binding.durationTextView.text = "00:00:00"

            val videoId = YouTubeUrlParser.extractVideoId(video.videoUrl)
            currentVideoId = videoId

            if (videoId != null) {
                val thumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
                Glide.with(binding.thumbnailImageView.context)
                    .load(thumbnailUrl)
                    .placeholder(R.color.placeholder_bg)
                    .into(binding.thumbnailImageView)

                initializePlayer()

                binding.thumbnailGroup.setOnClickListener {
                    youTubePlayer?.let { player ->
                        binding.thumbnailGroup.visibility = View.INVISIBLE
                        binding.youtubePlayerView.visibility = View.VISIBLE
                        player.loadVideo(videoId, 0f)
                    }
                }
            }
        }

        private fun initializePlayer() {
            binding.youtubePlayerView.addYouTubePlayerListener(object :
                AbstractYouTubePlayerListener() {
                override fun onReady(player: YouTubePlayer) {
                    youTubePlayer = player
                }
            })
        }

        private fun resetToThumbnail() {
            youTubePlayer?.pause()
            binding.youtubePlayerView.visibility = View.GONE
            binding.thumbnailGroup.visibility = View.VISIBLE
        }
    }

    object VideoDiffCallback : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean =
            oldItem == newItem
    }
}
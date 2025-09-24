package uz.umarov.fcneftchi.ui.videos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
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

    override fun onViewRecycled(holder: VideoViewHolder) {
        super.onViewRecycled(holder)
        holder.releasePlayer()
    }

    class VideoViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(video: Video) {
            binding.videoTitle.text = video.title

            val videoId = YouTubeUrlParser.extractVideoId(video.videoUrl)

            binding.youtubePlayerView.addYouTubePlayerListener(object :
                AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    videoId?.let {
                        youTubePlayer.cueVideo(it, 0f)
                    }
                }
            })
        }

        fun releasePlayer() {
            binding.youtubePlayerView.release()
        }
    }


    object VideoDiffCallback : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean =
            oldItem == newItem
    }
}
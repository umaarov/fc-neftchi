package uz.umarov.fcneftchi.ui.videos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.Video
import uz.umarov.fcneftchi.databinding.ItemVideoBinding
import uz.umarov.fcneftchi.util.YouTubeUrlParser

class VideoAdapter(
    private val lifecycle: Lifecycle,
    private val onBookmarkClick: (Video) -> Unit = {},
) : ListAdapter<Video, VideoAdapter.VideoViewHolder>(VideoDiffCallback) {

    private var bookmarkedIds: Set<String> = emptySet()

    fun submitBookmarkedIds(ids: Set<String>) {
        if (ids == bookmarkedIds) return
        bookmarkedIds = ids
        notifyItemRangeChanged(0, itemCount, PAYLOAD_BOOKMARK)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        lifecycle.addObserver(binding.youtubePlayerView)
        return VideoViewHolder(binding, onBookmarkClick)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = getItem(position)
        holder.bind(video, video.id in bookmarkedIds)
    }

    override fun onBindViewHolder(
        holder: VideoViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.contains(PAYLOAD_BOOKMARK)) {
            val video = getItem(position)
            holder.bindBookmark(video, video.id in bookmarkedIds)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    class VideoViewHolder(
        private val binding: ItemVideoBinding,
        private val onBookmarkClick: (Video) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        private var youTubePlayer: YouTubePlayer? = null
        private var currentVideoId: String? = null

        fun bind(video: Video, isBookmarked: Boolean) {
            resetToThumbnail()

            val context = binding.root.context
            binding.videoTitle.text = video.title
            binding.categoryTextView.text = context.getString(R.string.video_category_main_team)
            binding.dateTextView.text = video.date
            binding.durationTextView.text = context.getString(R.string.placeholder_duration)

            val videoId = YouTubeUrlParser.extractVideoId(video.videoUrl)
            currentVideoId = videoId

            if (videoId != null) {
                val thumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
                binding.thumbnailImageView.load(thumbnailUrl) {
                    placeholder(R.color.placeholder_bg)
                    error(R.color.placeholder_bg)
                    crossfade(true)
                }

                initializePlayer()

                binding.thumbnailGroup.setOnClickListener {
                    youTubePlayer?.let { player ->
                        binding.thumbnailGroup.visibility = View.INVISIBLE
                        binding.youtubePlayerView.visibility = View.VISIBLE
                        player.loadVideo(videoId, 0f)
                    }
                }
            }

            bindBookmark(video, isBookmarked)
        }

        fun bindBookmark(video: Video, isBookmarked: Boolean) {
            binding.bookmarkButton.setImageResource(
                if (isBookmarked) R.drawable.ic_bookmark_filled
                else R.drawable.ic_bookmark_border
            )
            binding.bookmarkButton.contentDescription = binding.root.context.getString(
                if (isBookmarked) R.string.bookmark_remove else R.string.bookmark_add
            )
            binding.bookmarkButton.setOnClickListener { onBookmarkClick(video) }
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

    private companion object {
        const val PAYLOAD_BOOKMARK = "payload_bookmark"
    }
}

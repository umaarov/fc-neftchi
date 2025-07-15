package uz.umarov.fcneftchi.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.databinding.ItemNewsHomeBinding

class NewsHomeAdapter(private val onItemClick: (NewsArticle) -> Unit) :
    ListAdapter<NewsArticle, NewsHomeAdapter.NewsViewHolder>(NewsDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsArticle = getItem(position)
        holder.bind(newsArticle)
        holder.itemView.setOnClickListener { onItemClick(newsArticle) }
    }

    inner class NewsViewHolder(private val binding: ItemNewsHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(newsArticle: NewsArticle) {
            binding.newsImage.load(newsArticle.imageUrl) {
                crossfade(true)
            }
            binding.newsTitle.text = newsArticle.title
            binding.newsDate.text = newsArticle.date
        }
    }

    object NewsDiffCallback : DiffUtil.ItemCallback<NewsArticle>() {
        override fun areItemsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
            return oldItem == newItem
        }
    }
}
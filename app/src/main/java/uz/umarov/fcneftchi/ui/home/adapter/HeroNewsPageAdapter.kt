package uz.umarov.fcneftchi.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.umarov.fcneftchi.data.model.NewsArticle
import uz.umarov.fcneftchi.databinding.ItemHeroCarouselPageBinding

class HeroNewsPagerAdapter(
    private val onArticleClick: (NewsArticle) -> Unit
) : ListAdapter<NewsArticle, HeroNewsPagerAdapter.HeroNewsPageViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroNewsPageViewHolder {
        val binding =
            ItemHeroCarouselPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeroNewsPageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeroNewsPageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HeroNewsPageViewHolder(private val binding: ItemHeroCarouselPageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: NewsArticle) {
            binding.root.setOnClickListener { onArticleClick(article) }
            binding.heroImage.load(article.imageUrl)
            binding.heroTitle.text = article.title
            binding.heroCategory.text = article.category

            binding.heroDate.text = "• ${article.date}"
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<NewsArticle>() {
        override fun areItemsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean =
            oldItem == newItem
    }
}
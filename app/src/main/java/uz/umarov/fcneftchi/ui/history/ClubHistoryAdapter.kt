package uz.umarov.fcneftchi.ui.history

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import uz.umarov.fcneftchi.databinding.ItemHistoryHeaderBinding
import uz.umarov.fcneftchi.databinding.ItemHistoryImageBinding
import uz.umarov.fcneftchi.databinding.ItemHistoryParagraphBinding
import uz.umarov.fcneftchi.databinding.ItemHistorySubheaderBinding
import uz.umarov.fcneftchi.databinding.ItemHistoryTrophyBinding

private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_SUBHEADER = 1
private const val VIEW_TYPE_PARAGRAPH = 2
private const val VIEW_TYPE_TROPHY = 3
private const val VIEW_TYPE_IMAGE = 4

class ClubHistoryAdapter(private val items: List<HistoryListItem>) :
    RecyclerView.Adapter<ClubHistoryAdapter.HistoryViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HistoryListItem.Header -> VIEW_TYPE_HEADER
            is HistoryListItem.SubHeader -> VIEW_TYPE_SUBHEADER
            is HistoryListItem.Paragraph -> VIEW_TYPE_PARAGRAPH
            is HistoryListItem.Trophy -> VIEW_TYPE_TROPHY
            is HistoryListItem.HistoryImage -> VIEW_TYPE_IMAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            VIEW_TYPE_HEADER -> ItemHistoryHeaderBinding.inflate(inflater, parent, false)
            VIEW_TYPE_SUBHEADER -> ItemHistorySubheaderBinding.inflate(inflater, parent, false)
            VIEW_TYPE_PARAGRAPH -> ItemHistoryParagraphBinding.inflate(inflater, parent, false)
            VIEW_TYPE_TROPHY -> ItemHistoryTrophyBinding.inflate(inflater, parent, false)
            VIEW_TYPE_IMAGE -> ItemHistoryImageBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("Invalid view type")
        }
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class HistoryViewHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryListItem) {
            when (binding) {
                is ItemHistoryHeaderBinding -> binding.headerText.text =
                    (item as HistoryListItem.Header).title

                is ItemHistorySubheaderBinding -> binding.subheaderText.text =
                    (item as HistoryListItem.SubHeader).title

                is ItemHistoryParagraphBinding -> binding.paragraphText.text = Html.fromHtml(
                    (item as HistoryListItem.Paragraph).text,
                    Html.FROM_HTML_MODE_COMPACT
                )

                is ItemHistoryTrophyBinding -> binding.trophyText.text =
                    (item as HistoryListItem.Trophy).description

                is ItemHistoryImageBinding -> binding.historyImage.setImageResource((item as HistoryListItem.HistoryImage).imageResId)
            }
        }
    }
}
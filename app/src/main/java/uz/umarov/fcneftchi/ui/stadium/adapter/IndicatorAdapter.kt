package uz.umarov.fcneftchi.ui.stadium.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.ItemIndicatorBinding

class IndicatorAdapter(private val itemCount: Int) :
    RecyclerView.Adapter<IndicatorAdapter.IndicatorViewHolder>() {

    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndicatorViewHolder {
        val binding =
            ItemIndicatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IndicatorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IndicatorViewHolder, position: Int) {
        holder.bind(position == selectedPosition)
    }

    override fun getItemCount(): Int = itemCount

    class IndicatorViewHolder(private val binding: ItemIndicatorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(isSelected: Boolean) {
            val background =
                if (isSelected) R.drawable.indicator_line_active else R.drawable.indicator_line_inactive
            binding.indicatorView.setBackgroundResource(background)
        }
    }
}
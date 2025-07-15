package uz.umarov.fcneftchi.ui.match_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.data.model.GameEvent
import uz.umarov.fcneftchi.databinding.ItemMatchEventBinding

class MatchEventsAdapter(
    private val homeTeamId: Int,
    private val playersMap: Map<Int, String>
) : ListAdapter<GameEvent, MatchEventsAdapter.EventViewHolder>(EventDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding =
            ItemMatchEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EventViewHolder(private val binding: ItemMatchEventBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: GameEvent) {
            val isHomeEvent = event.clubId == homeTeamId

            binding.homeEventGroup.isVisible = isHomeEvent
            binding.awayEventGroup.isVisible = !isHomeEvent

            val time = "${event.time}'"
            val primaryPlayer = playersMap[event.primaryPlayerId] ?: ""
            val secondaryPlayer = playersMap[event.secondaryPlayerId] ?: ""
            var eventText = primaryPlayer
            var eventIcon = 0

            when (event.type) {
                1 -> { // Goal
                    eventIcon = R.drawable.ic_goal
                }

                4 -> { // Yellow Card
                    eventIcon = R.drawable.ic_yellow_card
                }

                5 -> { // Red Card
                    eventIcon = R.drawable.ic_red_card
                }

                6 -> { // Substitution
                    eventIcon = R.drawable.ic_substitution
                    eventText = "$primaryPlayer  Substitution $secondaryPlayer"
                }
            }

            if (isHomeEvent) {
                binding.homeEventTime.text = time
                binding.homeEventText.text = eventText
                binding.homeEventIcon.setImageResource(eventIcon)
            } else {
                binding.awayEventTime.text = time
                binding.awayEventText.text = eventText
                binding.awayEventIcon.setImageResource(eventIcon)
            }
        }
    }

    object EventDiffCallback : DiffUtil.ItemCallback<GameEvent>() {
        override fun areItemsTheSame(oldItem: GameEvent, newItem: GameEvent): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: GameEvent, newItem: GameEvent): Boolean =
            oldItem == newItem
    }
}
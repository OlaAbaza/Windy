package com.example.windy.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.windy.databinding.FavItemBinding
import com.example.windy.models.domain.WeatherConditions

class FavoriteListAdapter(private val clickListener: FavoriteListener) :
    ListAdapter<WeatherConditions,
            FavoriteListAdapter.ViewHolder>(WeatherFavoriteDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: FavItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: FavoriteListener, item: WeatherConditions) {
            binding.weatherConditions = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FavItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

    class FavoriteListener(val clickListener: (weatherConditions: WeatherConditions) -> Unit) {
        fun onClick(weatherConditions: WeatherConditions) = clickListener(weatherConditions)
    }

}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minimum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
class WeatherFavoriteDiffCallback : DiffUtil.ItemCallback<WeatherConditions>() {
    override fun areItemsTheSame(oldItem: WeatherConditions, newItem: WeatherConditions): Boolean {
        return oldItem.timezone == newItem.timezone
    }

    override fun areContentsTheSame(
        oldItem: WeatherConditions,
        newItem: WeatherConditions
    ): Boolean {
        return oldItem == newItem
    }
}

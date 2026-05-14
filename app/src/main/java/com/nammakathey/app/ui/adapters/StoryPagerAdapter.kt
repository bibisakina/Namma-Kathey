package com.nammakathey.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nammakathey.app.databinding.ItemStoryPageBinding

class StoryPagerAdapter(private val pages: List<String>) : RecyclerView.Adapter<StoryPagerAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(val binding: ItemStoryPageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.binding.tvStoryContent.text = pages[position]
    }

    override fun getItemCount() = pages.size
}

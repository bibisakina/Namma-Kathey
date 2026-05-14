package com.nammakathey.app.ui.adapters

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nammakathey.app.data.model.Badge
import com.nammakathey.app.databinding.ItemBadgeBinding

class BadgeAdapter(private val badges: List<Badge>, private val userScore: Int) : RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    inner class BadgeViewHolder(val binding: ItemBadgeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val binding = ItemBadgeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BadgeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badges[position]
        holder.binding.tvBadgeName.text = badge.name
        holder.binding.tvBadgeDesc.text = badge.description

        if (userScore >= badge.requiredScore) {
            holder.binding.ivBadgeIcon.colorFilter = null
            holder.binding.tvBadgeName.alpha = 1.0f
            holder.binding.tvBadgeDesc.alpha = 1.0f
        } else {
            val matrix = ColorMatrix()
            matrix.setSaturation(0f)
            holder.binding.ivBadgeIcon.colorFilter = ColorMatrixColorFilter(matrix)
            holder.binding.tvBadgeName.alpha = 0.4f
            holder.binding.tvBadgeDesc.alpha = 0.4f
        }
    }

    override fun getItemCount() = badges.size
}

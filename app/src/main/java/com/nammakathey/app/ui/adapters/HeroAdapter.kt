package com.nammakathey.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nammakathey.app.R
import com.nammakathey.app.data.model.Hero
import com.nammakathey.app.databinding.ItemHeroBinding
import com.nammakathey.app.utils.SessionManager

class HeroAdapter(
    private var heroes: List<Hero>,
    private val onHeroClick: (Hero) -> Unit
) : RecyclerView.Adapter<HeroAdapter.HeroViewHolder>() {

    class HeroViewHolder(val binding: ItemHeroBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder {
        val binding = ItemHeroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeroViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        val hero = heroes[position]
        val context = holder.itemView.context
        val isKannada = SessionManager(context).getLanguage() == "kn"

        holder.binding.tvHeroName.text = if (isKannada) hero.nameKn else hero.nameEn
        holder.binding.tvCategory.text = if (isKannada) hero.categoryKn else hero.categoryEn
        
        // Fix: Use hero.photoRes directly from the Hero data model to ensure correct mapping
        val resourceId = context.resources.getIdentifier(hero.photoRes, "drawable", context.packageName)

        Glide.with(context)
            .load(if (resourceId != 0) resourceId else R.drawable.search_bg)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .centerCrop()
            .into(holder.binding.ivHero)

        holder.itemView.setOnClickListener {
            onHeroClick(hero)
        }
    }

    override fun getItemCount() = heroes.size
    
    fun updateData(newHeroes: List<Hero>) {
        heroes = newHeroes
        notifyDataSetChanged()
    }
}

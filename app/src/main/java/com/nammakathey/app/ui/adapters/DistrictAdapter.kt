package com.nammakathey.app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nammakathey.app.R
import com.nammakathey.app.data.model.District
import com.nammakathey.app.utils.SessionManager

class DistrictAdapter(
    private val districts: List<District>,
    private val onDistrictClick: (District) -> Unit
) : RecyclerView.Adapter<DistrictAdapter.DistrictViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistrictViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_district, parent, false)
        return DistrictViewHolder(view)
    }

    override fun onBindViewHolder(holder: DistrictViewHolder, position: Int) {
        val district = districts[position]
        val context = holder.itemView.context
        val isKannada = SessionManager(context).getLanguage() == "kn"

        holder.tvName.text = if (isKannada) district.nameKn else district.nameEn

        Glide.with(context)
            .load(district.imageRes)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .centerCrop()
            .into(holder.ivImage)

        holder.itemView.setOnClickListener {
            onDistrictClick(district)
        }
    }

    override fun getItemCount() = districts.size

    class DistrictViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivDistrict)
        val tvName: TextView = itemView.findViewById(R.id.tvDistrictName)
    }
}

package com.natan.klinik.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.natan.klinik.R
import com.natan.klinik.model.Ectoparasite

class EctoparasitAdapter(
    private val mContext: Context,
    private val items: List<Ectoparasite>,
    private val listener: onSelectData
) : RecyclerView.Adapter<EctoparasitAdapter.ViewHolder>() {

    interface onSelectData {
        fun onSelected(modelProduct: Ectoparasite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_ectoparasite, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Ectoparasite = items[position]

        // ✅ FIXED: Use renamed helper function
        val imageUrl = data.getFullImageUrl()

        Log.d("EctoparasitAdapter", "Loading image: $imageUrl for ${data.name}")

        // ✅ Load Image with proper error handling
        Glide.with(mContext)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.img_dog_guide) // Use existing drawable
            .error(R.drawable.img_dog_guide) // Use existing drawable
            .into(holder.imgProduct)

        holder.tvTitle.text = data.name

        holder.rlListProduct.setOnClickListener {
            listener.onSelected(data)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        var rlListProduct: RelativeLayout = itemView.findViewById(R.id.rootRelativeLayout)
        var imgProduct: ImageView = itemView.findViewById(R.id.imgHotel)
    }
}

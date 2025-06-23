package com.natan.klinik.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.natan.klinik.R
import com.natan.klinik.model.Scan
import com.natan.klinik.utils.ImageZoomDialog

class ScanHistoryAdapter(
    private val scans: List<Scan>,
    private val mContext: Context
) : RecyclerView.Adapter<ScanHistoryAdapter.ScanViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.scan_history_item, parent, false)
        return ScanViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScanViewHolder, position: Int) {
        val scan = scans[position]
        holder.bind(scan)
    }

    override fun getItemCount(): Int = scans.size

    inner class ScanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgPhoto: ImageView = itemView.findViewById(R.id.imgPhoto)

        fun bind(scan: Scan) {
            val imageUrl = "https://klinik.buatsoftware.com/storage/" + scan.photo

            // Load image dengan Glide
            Glide.with(mContext)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.img_doctor)
                .into(imgPhoto)

            // ✅ TAMBAHKAN: Click listener untuk zoom
            imgPhoto.setOnClickListener {
                showImageZoom(imageUrl)
            }

        }

        private fun showImageZoom(imageUrl: String) {
            val dialog = ImageZoomDialog(mContext, imageUrl)
            dialog.show()
        }
    }
}
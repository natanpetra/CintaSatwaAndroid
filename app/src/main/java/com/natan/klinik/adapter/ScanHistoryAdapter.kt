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
import com.natan.klinik.model.Order
import com.natan.klinik.model.Scan
import java.text.SimpleDateFormat
import java.util.Locale

class ScanHistoryAdapter(private val scans: List<Scan>, private val mContext: Context) :
    RecyclerView.Adapter<ScanHistoryAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.scan_history_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val scan = scans[position]
        holder.bind(scan)
    }

    override fun getItemCount(): Int = scans.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photo: ImageView = itemView.findViewById(R.id.imgPhoto)

        fun bind(scan: Scan) {
            Glide.with(mContext)
                .load("https://klinik.buatsoftware.com/storage/" + scan.photo) // Pastikan ini sesuai dengan field gambar yang benar
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.img_doctor)
                .into(photo)
        }

    }
}
package com.natan.klinik.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.natan.klinik.R
import com.natan.klinik.model.DoctorItem
import java.text.DecimalFormat

class DoctorAdapter(
    private val mContext: Context,
    private val items: List<DoctorItem>, // Pastikan hanya dideklarasikan di constructor
    private val listener: OnDoctorSelectedListener // Ubah nama variabel
) : RecyclerView.Adapter<DoctorAdapter.ViewHolder>() { // Tidak perlu tanda tanya di ViewHolder

    interface OnDoctorSelectedListener {
        fun onSelected(modelDoctor: DoctorItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_doctor, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size // Gunakan items.size, tidak perlu `itemCount`
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: DoctorItem = items[position]

        // Load Image dengan Glide
        Glide.with(mContext)
            .load(data.imageUrl) // Pastikan ini sesuai dengan field gambar yang benar
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.img_doctor)
            .into(holder.imgDoctor)

        holder.txtName.text = data.name
        holder.txtSpecialist.text = data.specialization

        holder.rlListDoctor.setOnClickListener {
            listener.onSelected(data) // Panggil tanpa View.OnClickListener { }
        }
    }

    // Class Holder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtSpecialist: TextView = itemView.findViewById(R.id.tvSpecialist)
        var txtName: TextView = itemView.findViewById(R.id.tvName)
        var rlListDoctor: CardView = itemView.findViewById(R.id.cvDoctor) // Pastikan ID benar
        var imgDoctor: ImageView = itemView.findViewById(R.id.imgDoctor)
    }
}



package com.natan.klinik.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.natan.klinik.R
import com.natan.klinik.model.Ras

/**
 * Created by Azhar Rivaldi on 22-12-2019.
 */

class RasAdapter(
    private val mContext: Context,
    private val items: List<Ras>,
    private val listener: onSelectData // Simpan sebagai properti class
) : RecyclerView.Adapter<RasAdapter.ViewHolder>() {

    // Interface untuk menangani klik item
    interface onSelectData {
        fun onSelected(modelProduct: Ras) // Tidak nullable untuk menghindari error
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_ectoparasite, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size // Gunakan langsung tanpa deklarasi ulang itemCount
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Ras = items[position]

        holder.tvTitle.text = data.name

    }

    // Class Holder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    }
}


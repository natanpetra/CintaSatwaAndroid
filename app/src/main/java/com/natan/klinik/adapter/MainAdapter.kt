package com.natan.klinik.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.natan.klinik.R
import com.natan.klinik.model.ModelMain

/**
 * Created by Azhar Rivaldi on 22-12-2019.
 */
class MainAdapter(
    private val items: MutableList<ModelMain?>,
    private val onSelectData: OnSelectData // Ubah nama interface ke PascalCase
) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() { // Ubah ViewHolder jadi MainViewHolder

    interface OnSelectData { // Perbaikan nama interface
        fun onSelected(mdlMain: ModelMain?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_grid_main, parent, false)
        return MainViewHolder(v) // Gunakan MainViewHolder
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val data = items[position]
        holder.imgMainData.setImageResource(data!!.imgSrc)
        holder.tvMainData.text = data.txtName
        holder.cvMainData.setOnClickListener {
            onSelectData.onSelected(data)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // Class Holder - Ubah nama jadi MainViewHolder agar tidak bentrok
    inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvMainData: CardView = itemView.findViewById(R.id.cvMainData)
        val tvMainData: TextView = itemView.findViewById(R.id.tvMainData)
        val imgMainData: ImageView = itemView.findViewById(R.id.imgMainData)
    }
}


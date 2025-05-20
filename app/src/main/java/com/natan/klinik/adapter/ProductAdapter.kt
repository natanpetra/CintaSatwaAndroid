package com.natan.klinik.adapter

import android.content.Context
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
import com.natan.klinik.model.ProductItem
import java.text.DecimalFormat

/**
 * Created by Azhar Rivaldi on 22-12-2019.
 */

class ProductAdapter(
    private val mContext: Context,
    private val items: List<ProductItem>,
    private val listener: onSelectData // Simpan sebagai properti class
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    // Interface untuk menangani klik item
    interface onSelectData {
        fun onSelected(modelProduct: ProductItem) // Tidak nullable untuk menghindari error
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_product, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size // Gunakan langsung tanpa deklarasi ulang itemCount
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: ProductItem = items[position]

        // Load Image dengan Glide
        Glide.with(mContext)
            .load(data.imageUrl) // Pastikan ini adalah field gambar yang benar
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.imgProduct) // Sesuaikan ID gambar

        holder.tvNamaProduct.text = data.name
        holder.txtPrice.text = "Rp. ${data.price}"

        holder.rlListProduct.setOnClickListener {
            listener.onSelected(data) // Gunakan dengan benar
        }
    }

    // Class Holder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvNamaProduct: TextView = itemView.findViewById(R.id.tvNamaHotel)
        var txtPrice: TextView = itemView.findViewById(R.id.tvJarak) // Sesuaikan ID harga
        var rlListProduct: RelativeLayout = itemView.findViewById(R.id.rlListHotel)
        var imgProduct: ImageView = itemView.findViewById(R.id.imgHotel)
    }
}


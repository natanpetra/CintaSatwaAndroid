package com.natan.klinik.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.natan.klinik.R
import com.natan.klinik.activities.DetailObatActivity
import com.natan.klinik.model.ProductItem

class ObatAdapter(
    private val context: Context,
    private val data: List<ProductItem>
) : BaseAdapter() {

    override fun getCount(): Int = data.size
    override fun getItem(position: Int): Any = data[position]
    override fun getItemId(position: Int): Long = data[position].id!!.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_obat, parent, false)
        val item = data[position]

        val nama = view.findViewById<TextView>(R.id.tvNamaObat)
        val harga = view.findViewById<TextView>(R.id.tvHargaObat)
        val image = view.findViewById<ImageView>(R.id.imgObat)

        nama.text = item.name
        harga.text = "Rp ${item.price}"
        Glide.with(context).load(item.imageUrl).into(image)

        view.setOnClickListener {
            val intent = Intent(context, DetailObatActivity::class.java)
            intent.putExtra("obat", Gson().toJson(item)) // kirim sebagai JSON string
            context.startActivity(intent)
        }

        return view
    }
}

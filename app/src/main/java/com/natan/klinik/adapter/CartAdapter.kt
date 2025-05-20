package com.natan.klinik.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.natan.klinik.R
import com.natan.klinik.model.ProductItem
import com.natan.klinik.utils.CartManager

class CartAdapter(
    private val items: List<ProductItem>,
    private val onItemChanged: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val btnIncrease: View = itemView.findViewById(R.id.btnIncrease)
        val btnDecrease: View = itemView.findViewById(R.id.btnDecrease)
        val btnRemove: View = itemView.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]

        holder.tvName.text = item.name ?: "Unknown Product"
        holder.tvPrice.text = "Rp ${item.price ?: "0"}"
        holder.tvQuantity.text = item.quantity?.toString() ?: "0"

        holder.btnIncrease.setOnClickListener {
            item.quantity = (item.quantity ?: 0) + 1
            notifyItemChanged(position)
            onItemChanged()
        }

        holder.btnDecrease.setOnClickListener {
            if ((item.quantity ?: 0) > 1) {
                item.quantity = (item.quantity ?: 0) - 1
                notifyItemChanged(position)
                onItemChanged()
            }
        }

        holder.btnRemove.setOnClickListener {
            CartManager.removeItem(item.id ?: 0)
            notifyItemRemoved(position)
            onItemChanged()
        }
    }

    override fun getItemCount(): Int = items.size
}
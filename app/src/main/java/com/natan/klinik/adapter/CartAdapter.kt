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
    private val items: MutableList<ProductItem>,
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

        // ✅ Format harga dengan quantity
        val pricePerItem = item.price?.toDoubleOrNull() ?: 0.0
        val quantity = item.quantity ?: 0
        val totalItemPrice = pricePerItem * quantity

        holder.tvPrice.text = "Rp ${String.format("%,.0f", pricePerItem)} x $quantity = Rp ${String.format("%,.0f", totalItemPrice)}"
        holder.tvQuantity.text = quantity.toString()

        // ✅ Increase quantity
        holder.btnIncrease.setOnClickListener {
            item.quantity = (item.quantity ?: 0) + 1
            CartManager.updateQuantity(item.id ?: 0, item.quantity ?: 1)
            notifyItemChanged(position)
            onItemChanged()
        }

        // ✅ Decrease quantity
        holder.btnDecrease.setOnClickListener {
            if ((item.quantity ?: 0) > 1) {
                item.quantity = (item.quantity ?: 0) - 1
                CartManager.updateQuantity(item.id ?: 0, item.quantity ?: 1)
                notifyItemChanged(position)
                onItemChanged()
            }
        }

        // ✅ PERBAIKAN: Remove item dengan proper handling
        holder.btnRemove.setOnClickListener {
            val itemId = item.id ?: 0
            val currentPosition = holder.adapterPosition

            if (currentPosition != RecyclerView.NO_POSITION) {
                // Hapus dari CartManager
                CartManager.removeItem(itemId)

                // Hapus dari local list
                items.removeAt(currentPosition)

                // Notify adapter
                notifyItemRemoved(currentPosition)
                notifyItemRangeChanged(currentPosition, items.size)

                // Update total price
                onItemChanged()
            }
        }
    }

    override fun getItemCount(): Int = items.size

    // ✅ Method untuk refresh data dari CartManager
    fun refreshData() {
        items.clear()
        items.addAll(CartManager.getCartItems())
        notifyDataSetChanged()
    }
}
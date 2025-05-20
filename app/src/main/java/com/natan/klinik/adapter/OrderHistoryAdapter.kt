package com.natan.klinik.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.natan.klinik.R
import com.natan.klinik.model.Order
import java.text.SimpleDateFormat
import java.util.Locale

class OrderHistoryAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.riwayat_pembelian_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTransactionDate: TextView = itemView.findViewById(R.id.tvTransactionDate)
        private val tvTransactionId: TextView = itemView.findViewById(R.id.tvTransactionId)
        private val tvItems: TextView = itemView.findViewById(R.id.tvItems)
        private val tvTotalPrice: TextView = itemView.findViewById(R.id.tvTotalPrice)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(order: Order) {
            tvTransactionDate.text = formatDate(order.createdAt)
            tvTransactionId.text = " | ID: #${order.id}"
            tvTotalPrice.text = "Total: Rp ${order.totalPrice}"
            tvStatus.text = order.status

            // Gabungkan nama produk dari orderItems
            val itemNames = order.orderItems?.joinToString {
                "${it.product?.name ?: "Product"} (x${it.quantity})"
            } ?: "No items"
            tvItems.text = itemNames
        }

        private fun formatDate(dateString: String): String {
            // Konversi format tanggal (contoh: ISO 8601 → "15 Oktober 2023")
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                outputFormat.format(date!!)
            } catch (e: Exception) {
                dateString
            }
        }
    }
}
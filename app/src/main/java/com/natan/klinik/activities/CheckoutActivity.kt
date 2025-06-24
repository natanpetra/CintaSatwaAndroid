package com.natan.klinik.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.natan.klinik.adapter.CartAdapter
import com.natan.klinik.databinding.ActivityCheckoutBinding
import com.natan.klinik.model.ApiResponse
import com.natan.klinik.model.CheckoutRequest
import com.natan.klinik.model.OrderItemRequest
import com.natan.klinik.model.ProductItem
import com.natan.klinik.network.RetrofitClient
import com.natan.klinik.utils.CartManager
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var cartAdapter: CartAdapter
    private var cartItems: MutableList<ProductItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        loadCartItems()
        setupCheckoutButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Checkout"
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(cartItems) {
            updateTotalPrice()
            checkEmptyCart()
        }
        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(this@CheckoutActivity)
            adapter = cartAdapter
        }
    }

    private fun loadCartItems() {
        cartItems.clear()
        cartItems.addAll(CartManager.getCartItems())
        cartAdapter.notifyDataSetChanged()
        updateTotalPrice()
        checkEmptyCart()

        // ✅ Debug log
        Log.d("Checkout", "Loaded ${cartItems.size} items")
        CartManager.printCartItems()
    }

    private fun updateTotalPrice() {
        val totalPrice = cartItems.sumOf {
            (it.price?.toDoubleOrNull() ?: 0.0) * (it.quantity ?: 0)
        }
        binding.tvTotalPrice.text = "Total: Rp ${String.format("%,.0f", totalPrice)}"

        // ✅ Debug log
        Log.d("Checkout", "Total Price: $totalPrice")
    }

    // ✅ Check jika cart kosong
    private fun checkEmptyCart() {
        if (cartItems.isEmpty()) {
            binding.tvTotalPrice.text = "Keranjang kosong"
            Toast.makeText(this, "Keranjang belanja kosong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCheckoutButton() {
        binding.btnCheckout.setOnClickListener {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Keranjang belanja kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            processCheckout()
        }
    }

    private fun processCheckout() {
        val userId = Prefs.getInt("user_id", 0)

        if (userId == 0) {
            Toast.makeText(this, "User ID tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val totalPrice = cartItems.sumOf {
            (it.price?.toDoubleOrNull() ?: 0.0) * (it.quantity ?: 0)
        }

        val orderItems = cartItems.map { product ->
            OrderItemRequest(
                product_id = product.id ?: 0,
                quantity = product.quantity ?: 1
            )
        }

        val checkoutRequest = CheckoutRequest(
            user_id = userId,
            items = orderItems,
            total_price = totalPrice
        )

        // ✅ Debug log
        Log.d("Checkout", "Processing checkout for user: $userId, total: $totalPrice")

        RetrofitClient.instance.checkout(checkoutRequest).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    when (apiResponse?.status) {
                        "success" -> {
                            Toast.makeText(
                                this@CheckoutActivity,
                                "Checkout berhasil! Order ID: ${apiResponse.order_id}",
                                Toast.LENGTH_LONG
                            ).show()

                            // ✅ ENHANCED: Send detailed WhatsApp message with items
                            sendDetailedWhatsAppReminderToSeller(
                                orderId = apiResponse.order_id.toString(),
                                customerName = Prefs.getString("name", "Customer"),
                                customerPhone = Prefs.getString("phone", "-"),
                                totalAmount = String.format("%,.0f", apiResponse.total_price ?: totalPrice),
                                cartItems = cartItems
                            )

                            // ✅ Clear cart setelah checkout berhasil
                            CartManager.clearCart()
                            cartItems.clear()
                            cartAdapter.notifyDataSetChanged()

                            finish()
                        }
                        else -> {
                            Toast.makeText(
                                this@CheckoutActivity,
                                apiResponse?.message ?: "Checkout gagal",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@CheckoutActivity,
                        "Error: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(
                    this@CheckoutActivity,
                    "Network error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // ✅ ENHANCED: Detailed WhatsApp message with item details
    private fun sendDetailedWhatsAppReminderToSeller(
        orderId: String,
        customerName: String,
        customerPhone: String,
        totalAmount: String,
        cartItems: List<ProductItem>
    ) {
        try {
            val sellerPhone = "6281353941310" // ✅ Ganti dengan nomor admin yang benar

            // ✅ Get current date and time
            val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
            val currentDateTime = dateFormat.format(Date())

            // ✅ Build detailed items list
            val itemsDetails = StringBuilder()
            var totalItems = 0

            cartItems.forEachIndexed { index, item ->
                val itemTotal = (item.price?.toDoubleOrNull() ?: 0.0) * (item.quantity ?: 0)
                totalItems += (item.quantity ?: 0)

                itemsDetails.append("${index + 1}. ${item.name}\n")
                itemsDetails.append("   Qty: ${item.quantity} x Rp ${String.format("%,.0f", item.price?.toDoubleOrNull() ?: 0.0)}\n")
                itemsDetails.append("   Subtotal: Rp ${String.format("%,.0f", itemTotal)}\n")
                if (index < cartItems.size - 1) itemsDetails.append("\n")
            }

            // ✅ Build comprehensive message
            val message = """
🚨 *PESANAN BARU MASUK!* 🚨

📋 *DETAIL PESANAN*
━━━━━━━━━━━━━━━━━━━━━
🆔 Order ID: #$orderId
📅 Tanggal: $currentDateTime
👤 Customer: $customerName
📱 No. HP: $customerPhone

🛒 *DAFTAR BARANG ($totalItems item)*
━━━━━━━━━━━━━━━━━━━━━
$itemsDetails

💰 *TOTAL PEMBAYARAN*
━━━━━━━━━━━━━━━━━━━━━
💵 Rp $totalAmount

⚡ *SEGERA PROSES PESANAN INI!*

Terima kasih 🙏
            """.trimIndent()

            val whatsappUrl = "https://wa.me/$sellerPhone?text=${URLEncoder.encode(message, "UTF-8")}"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
            startActivity(browserIntent)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal mengirim reminder: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ Override onResume untuk refresh data
    override fun onResume() {
        super.onResume()
        loadCartItems()
    }
}
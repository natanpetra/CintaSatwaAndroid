package com.natan.klinik.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.natan.klinik.R
import com.natan.klinik.adapter.DoctorAdapter
import com.natan.klinik.adapter.ProductAdapter
import com.natan.klinik.model.DoctorItem
import com.natan.klinik.model.ProductItem
import com.natan.klinik.network.RetrofitClient
import com.natan.klinik.utils.CartManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductListActivity : AppCompatActivity(), ProductAdapter.onSelectData {
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var adapter: ProductAdapter
    private var productList: MutableList<ProductItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_list)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Product List")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        recyclerView = findViewById(R.id.rvDoctor)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchProduct()
    }

    private fun fetchProduct() {
        RetrofitClient.instance.getProduct().enqueue(object : Callback<List<ProductItem>> {
            override fun onResponse(call: Call<List<ProductItem>>, response: Response<List<ProductItem>>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        productList.addAll(data)
                        adapter = ProductAdapter(this@ProductListActivity, productList, this@ProductListActivity)
                        recyclerView.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<List<ProductItem>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun onSelected(modelProduct: ProductItem) {
        showQuantityDialog(modelProduct)
    }

    private fun showQuantityDialog(product: ProductItem) {
        val dialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_quantity, null)

        val tvProductName: TextView = dialogView.findViewById(R.id.tvProductName)
        val tvPrice: TextView = dialogView.findViewById(R.id.tvPrice)
        val etQuantity: EditText = dialogView.findViewById(R.id.etQuantity)

        tvProductName.text = product.name
        tvPrice.text = "Rp ${product.price}"
        etQuantity.setText("1")

        dialog.setView(dialogView)
            .setTitle("Tambah ke Keranjang")
            .setPositiveButton("Tambah") { _, _ ->
                val quantity = etQuantity.text.toString().toIntOrNull() ?: 1
                if (quantity > 0) {
                    val productToAdd = product.copy(quantity = quantity)
                    CartManager.addToCart(productToAdd)
                    Toast.makeText(this, "Ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_product, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_cart -> {
                startActivity(Intent(this, CheckoutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
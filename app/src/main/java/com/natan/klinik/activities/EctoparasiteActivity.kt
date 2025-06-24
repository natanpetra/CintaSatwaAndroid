package com.natan.klinik.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.natan.klinik.R
import com.natan.klinik.adapter.EctoparasitAdapter
import com.natan.klinik.model.Ectoparasite
import com.natan.klinik.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EctoparasiteActivity : AppCompatActivity(), EctoparasitAdapter.onSelectData {

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var adapter: EctoparasitAdapter
    private var productList: MutableList<Ectoparasite> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog_guide)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Dog Ectoparasite")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        recyclerView = findViewById(R.id.rvGuide)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchProduct()
    }

    private fun fetchProduct() {
        Log.d("EctoparasiteActivity", "Fetching ectoparasite data...")

        RetrofitClient.instance.getEctoparasite().enqueue(object : Callback<List<Ectoparasite>> {
            override fun onResponse(call: Call<List<Ectoparasite>>, response: Response<List<Ectoparasite>>) {
                Log.d("EctoparasiteActivity", "Response code: ${response.code()}")

                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null && data.isNotEmpty()) {
                        Log.d("EctoparasiteActivity", "Received ${data.size} ectoparasites")

                        // ✅ Debug each item with renamed function
                        data.forEachIndexed { index, ectoparasite ->
                            Log.d("EctoparasiteActivity", "[$index] Name: ${ectoparasite.name}")
                            Log.d("EctoparasiteActivity", "[$index] Image: ${ectoparasite.image}")
                            Log.d("EctoparasiteActivity", "[$index] ImageURL: ${ectoparasite.imageUrl}")
                            Log.d("EctoparasiteActivity", "[$index] Final URL: ${ectoparasite.getFullImageUrl()}")
                        }

                        productList.clear()
                        productList.addAll(data)
                        adapter = EctoparasitAdapter(this@EctoparasiteActivity, productList, this@EctoparasiteActivity)
                        recyclerView.adapter = adapter
                    } else {
                        Log.w("EctoparasiteActivity", "Data is null or empty")
                        Toast.makeText(this@EctoparasiteActivity, "No ectoparasite data found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("EctoparasiteActivity", "Response not successful: ${response.code()}")
                    try {
                        Log.e("EctoparasiteActivity", "Error body: ${response.errorBody()?.string()}")
                    } catch (e: Exception) {
                        Log.e("EctoparasiteActivity", "Error reading error body", e)
                    }
                    Toast.makeText(this@EctoparasiteActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Ectoparasite>>, t: Throwable) {
                Log.e("EctoparasiteActivity", "API call failed", t)
                Toast.makeText(this@EctoparasiteActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSelected(modelProduct: Ectoparasite) {
        Log.d("EctoparasiteActivity", "Selected: ${modelProduct.name}")
        val intent = Intent(this, DetailDogEctoparasiteActivity::class.java)
        intent.putExtra("ectoparasite", modelProduct)
        startActivity(intent)
    }
}
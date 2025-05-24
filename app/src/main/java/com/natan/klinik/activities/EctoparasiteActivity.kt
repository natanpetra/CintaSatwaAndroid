package com.natan.klinik.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.natan.klinik.R
import com.natan.klinik.adapter.EctoparasitAdapter
import com.natan.klinik.adapter.ProductAdapter
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
        RetrofitClient.instance.getEctoparasite().enqueue(object : Callback<List<Ectoparasite>> {
            override fun onResponse(call: Call<List<Ectoparasite>>, response: Response<List<Ectoparasite>>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        productList.addAll(data)
                        adapter = EctoparasitAdapter(this@EctoparasiteActivity, productList, this@EctoparasiteActivity)
                        recyclerView.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<List<Ectoparasite>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun onSelected(modelProduct: Ectoparasite) {
        val intent = Intent(this, DetailDogEctoparasiteActivity::class.java)
        intent.putExtra("ectoparasite", modelProduct)
        startActivity(intent)
    }
}
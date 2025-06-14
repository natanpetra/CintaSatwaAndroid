package com.natan.klinik.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.natan.klinik.R
import com.natan.klinik.adapter.EctoparasitAdapter
import com.natan.klinik.adapter.RasAdapter
import com.natan.klinik.model.Ectoparasite
import com.natan.klinik.model.Ras
import com.natan.klinik.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RasActivity : AppCompatActivity(), RasAdapter.onSelectData {
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var adapter: RasAdapter
    private var productList: MutableList<Ras> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ras)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Ras")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        recyclerView = findViewById(R.id.rvRas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchProduct()
    }

    private fun fetchProduct() {
        RetrofitClient.instance.getRas().enqueue(object : Callback<List<Ras>> {
            override fun onResponse(call: Call<List<Ras>>, response: Response<List<Ras>>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        productList.addAll(data)
                        adapter = RasAdapter(this@RasActivity, productList, this@RasActivity)
                        recyclerView.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<List<Ras>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    override fun onSelected(modelProduct: Ras) {
        TODO("Not yet implemented")
    }
}
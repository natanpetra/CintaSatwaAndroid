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
import com.natan.klinik.adapter.GuideAdapter
import com.natan.klinik.adapter.ProductAdapter
import com.natan.klinik.model.Guide
import com.natan.klinik.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DogGuideActivity : AppCompatActivity(), GuideAdapter.onSelectData {

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var adapter: GuideAdapter
    private var productList: MutableList<Guide> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog_guide)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Dog Guide")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        recyclerView = findViewById(R.id.rvGuide)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchProduct()
    }

    private fun fetchProduct() {
        RetrofitClient.instance.getGuide().enqueue(object : Callback<List<Guide>> {
            override fun onResponse(call: Call<List<Guide>>, response: Response<List<Guide>>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        productList.addAll(data)
                        adapter = GuideAdapter(this@DogGuideActivity, productList, this@DogGuideActivity)
                        recyclerView.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<List<Guide>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun onSelected(modelProduct: Guide) {
        val intent = Intent(this, DetailDogGuideActivity::class.java)
        intent.putExtra("guide", modelProduct)
        startActivity(intent)
    }
}
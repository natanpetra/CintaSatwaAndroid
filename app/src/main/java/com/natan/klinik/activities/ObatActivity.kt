package com.natan.klinik.activities

import android.os.Bundle
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.natan.klinik.R
import com.natan.klinik.adapter.ObatAdapter
import com.natan.klinik.adapter.ProductAdapter
import com.natan.klinik.databinding.ActivityObatBinding
import com.natan.klinik.model.ProductItem
import com.natan.klinik.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ObatActivity : AppCompatActivity() {
    private lateinit var gridView: GridView
    private lateinit var progressBar: ProgressBar
    lateinit var binding: ActivityObatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityObatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gridView = findViewById(R.id.gridViewObat)

        RetrofitClient.instance.getProduct().enqueue(object : Callback<List<ProductItem>> {
            override fun onResponse(call: Call<List<ProductItem>>, response: Response<List<ProductItem>>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        gridView.adapter = ObatAdapter(this@ObatActivity, data)
                    }
                }
            }

            override fun onFailure(call: Call<List<ProductItem>>, t: Throwable) {
                t.printStackTrace()
            }
        })

    }
}
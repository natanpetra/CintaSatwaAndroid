package com.natan.klinik.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.natan.klinik.R
import com.natan.klinik.adapter.DoctorAdapter
import com.natan.klinik.model.DoctorItem
import com.natan.klinik.network.ApiService
import com.natan.klinik.network.RetrofitClient
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DoctorListActivity : AppCompatActivity(), DoctorAdapter.OnDoctorSelectedListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var adapter: DoctorAdapter
    private var doctorList: MutableList<DoctorItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_list)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Doctor List")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        recyclerView = findViewById(R.id.rvDoctor)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchDoctors()
    }

    private fun fetchDoctors() {
        RetrofitClient.instance.getDoctor().enqueue(object : Callback<List<DoctorItem>> {
            override fun onResponse(call: Call<List<DoctorItem>>, response: Response<List<DoctorItem>>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        doctorList.addAll(data)
                        adapter = DoctorAdapter(this@DoctorListActivity, doctorList, this@DoctorListActivity)
                        recyclerView.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<List<DoctorItem>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun onSelected(modelDoctor: DoctorItem) {
        // Handle the selected doctor item
        Toast.makeText(this, "Selected: ${modelDoctor.name}", Toast.LENGTH_SHORT).show()
        // You can start a new activity or show details here
         val intent = Intent(this, DetailDoctorActivity::class.java)
         intent.putExtra("doctor", modelDoctor)
         startActivity(intent)
    }
}

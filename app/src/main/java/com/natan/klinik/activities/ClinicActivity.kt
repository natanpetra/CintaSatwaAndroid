package com.natan.klinik.activities

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.natan.klinik.R
import com.natan.klinik.adapter.GuideAdapter
import com.natan.klinik.model.Clinic
import com.natan.klinik.model.Guide
import com.natan.klinik.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClinicActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var tvSchedule: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clinic)

        toolbar = findViewById(R.id.toolbar)
        tvSchedule = findViewById(R.id.tvClinicSchedule)
        tvPhone = findViewById(R.id.tvClinicPhone)
        tvAddress = findViewById(R.id.tvClinicAddress)
        tvName = findViewById(R.id.tvClinicName)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Clinic")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }



        RetrofitClient.instance.getClinic().enqueue(object : Callback<Clinic> {
            override fun onResponse(call: Call<Clinic>, response: Response<Clinic>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        tvAddress.text = data.address
                        tvPhone.text = data.phone
                        tvSchedule.text = data.schedule
                        tvName.text = data.name
                    }
                }
            }

            override fun onFailure(call: Call<Clinic>, t: Throwable) {
                t.printStackTrace()
            }
        })


    }
}
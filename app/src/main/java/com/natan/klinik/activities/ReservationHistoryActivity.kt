package com.natan.klinik.activities

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.natan.klinik.R
import com.natan.klinik.adapter.ReservationAdapter
import com.natan.klinik.model.Reservation
import com.natan.klinik.network.RetrofitClient
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReservationHistoryActivity : AppCompatActivity() {
    private lateinit var rvReservations: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: ReservationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_history)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Riwayat Reservasi")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Initialize views
        rvReservations = findViewById(R.id.rvReservations)
        progressBar = findViewById(R.id.progressBar)
        tvEmpty = findViewById(R.id.tvEmpty)

        setupRecyclerView()
        loadReservations()
    }

    private fun setupRecyclerView() {
        rvReservations.layoutManager = LinearLayoutManager(this)
    }

    private fun loadReservations() {
        progressBar.visibility = View.VISIBLE
        val userId = Prefs.getInt("user_id", 0)

        RetrofitClient.instance.getReservationHistory(userId).enqueue(object : Callback<List<Reservation>> {
            override fun onResponse(call: Call<List<Reservation>>, response: Response<List<Reservation>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val reservations = response.body()
                    if (reservations != null && reservations.isNotEmpty()) {
                        tvEmpty.visibility = View.GONE
                        rvReservations.visibility = View.VISIBLE
                        setupAdapter(reservations)
                    } else {
                        rvReservations.visibility = View.GONE
                        tvEmpty.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(
                        this@ReservationHistoryActivity,
                        "Gagal memuat data reservasi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Reservation>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this@ReservationHistoryActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setupAdapter(reservations: List<Reservation>) {
        adapter = ReservationAdapter(reservations)
        rvReservations.adapter = adapter
    }
}
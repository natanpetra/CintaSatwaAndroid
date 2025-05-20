package com.natan.klinik.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.natan.klinik.R
import com.natan.klinik.databinding.ActivityReservasiBinding
import com.natan.klinik.model.ApiResponse
import com.natan.klinik.network.RetrofitClient
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReservasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReservasiBinding
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private val timeButtons = mutableListOf<Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Periksa user ID di awal activity
        val userId = Prefs.getInt("user_id", 0)
        Log.d("ReservasiActivity", "onCreate - userId: $userId")

        // Jika tidak ada user ID, arahkan ke login
        if (userId == 0) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setTitle("Reservasi Online")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Setup form and calendar view
        setupReservationForm()
    }

    private fun setupReservationForm() {
        // Tambahkan semua tombol waktu ke list
        timeButtons.add(binding.btnTime1)
        timeButtons.add(binding.btnTime2)
        timeButtons.add(binding.btnTime3)
        timeButtons.add(binding.btnTime4)
        timeButtons.add(binding.btnTime5)

        // Setup calendar untuk pemilihan tanggal
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        // Atur tanggal minimum ke hari ini
        binding.calendarView.minDate = calendar.timeInMillis

        // Default tanggal ke hari ini
        selectedDate = dateFormat.format(calendar.time)

        // Listener untuk pemilihan tanggal
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = dateFormat.format(calendar.time)
            Log.d("ReservasiActivity", "Tanggal dipilih: $selectedDate")
        }

        // Atur listener untuk tombol waktu
        for (button in timeButtons) {
            button.setOnClickListener {
                // Reset warna semua tombol
                resetTimeButtonsBackground()
                // Atur warna tombol yang dipilih
                it.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                selectedTime = (it as Button).text.toString()
                Log.d("ReservasiActivity", "Waktu dipilih: $selectedTime")
            }
        }

        // Handle form submission
        binding.btnSubmit.setOnClickListener {
            if (validateAndSubmitForm()) {
                submitReservation()
            }
        }
    }

    private fun resetTimeButtonsBackground() {
        for (button in timeButtons) {
            button.setBackgroundResource(android.R.drawable.btn_default)
        }
    }

    private fun validateAndSubmitForm(): Boolean {
        val petName = binding.etPetName.text.toString().trim()
        val petType = binding.etPetType.text.toString().trim()
        val symptoms = binding.etSymptoms.text.toString().trim()

        // Validasi data yang diinputkan
        if (petName.isEmpty()) {
            binding.etPetName.error = "Nama hewan tidak boleh kosong"
            return false
        }

        if (petType.isEmpty()) {
            binding.etPetType.error = "Jenis hewan tidak boleh kosong"
            return false
        }

        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Silakan pilih tanggal kunjungan", Toast.LENGTH_SHORT).show()
            return false
        }

        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Silakan pilih waktu kunjungan", Toast.LENGTH_SHORT).show()
            return false
        }

        if (symptoms.isEmpty()) {
            binding.etSymptoms.error = "Keluhan/gejala tidak boleh kosong"
            return false
        }

        return true
    }

    private fun submitReservation() {
        // Ambil ID pengguna dari SharedPreferences
        val userId = Prefs.getInt("user_id", 0)

        // Log untuk debugging
        Log.d("ReservasiActivity", "userId dari Prefs: $userId")

        // Cek apakah userId valid
        if (userId == 0) {
            Toast.makeText(
                this@ReservasiActivity,
                "Anda tidak memiliki ID pengguna yang valid. Silakan login terlebih dahulu.",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val petName = binding.etPetName.text.toString().trim()
        val petType = binding.etPetType.text.toString().trim()
        val symptoms = binding.etSymptoms.text.toString().trim()

        // Log informasi sebelum mengirim
        Log.d("ReservasiActivity", "Mengirim reservasi: userId=$userId, petName=$petName, petType=$petType, date=$selectedDate, time=$selectedTime, symptoms=$symptoms")

        // Kirim data ke server menggunakan Retrofit
        RetrofitClient.instance.createReservation(
            userId,
            petName,
            petType,
            selectedDate,
            selectedTime,
            symptoms
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                // Log informasi respons
                Log.d("ReservasiActivity", "Kode respons: ${response.code()}")
                Log.d("ReservasiActivity", "Body: ${response.body()}")

                try {
                    Log.d("ReservasiActivity", "Error body: ${response.errorBody()?.string()}")
                } catch (e: Exception) {
                    Log.e("ReservasiActivity", "Error reading error body", e)
                }

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d("ReservasiActivity", "Respons sukses: ${apiResponse?.message}")
                    Toast.makeText(
                        this@ReservasiActivity,
                        "Reservasi berhasil dibuat!",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    Log.e("ReservasiActivity", "Respons gagal: ${response.code()}")
                    Toast.makeText(
                        this@ReservasiActivity,
                        "Gagal membuat reservasi. Silakan coba lagi. (${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                // Log informasi kesalahan
                Log.e("ReservasiActivity", "Error: ${t.message}", t)
                Toast.makeText(
                    this@ReservasiActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
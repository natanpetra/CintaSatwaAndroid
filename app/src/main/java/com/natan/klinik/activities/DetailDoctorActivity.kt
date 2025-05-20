package com.natan.klinik.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.natan.klinik.R
import com.natan.klinik.databinding.ActivityDetailDoctorBinding
import com.natan.klinik.model.DoctorItem
import java.net.URLEncoder

class DetailDoctorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailDoctorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setTitle("Detail Doctor")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val doctor = intent.getSerializableExtra("doctor") as? DoctorItem

        if (doctor != null) {
            binding.tvDoctorNameDetail.text = doctor.name
            binding.tvSpecialistDetail.text = doctor.specialization
            binding.tvDescription.text = doctor.phone
            Glide.with(this@DetailDoctorActivity)
                .load(doctor.imageUrl) // Pastikan ini sesuai dengan field gambar yang benar
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.img_doctor)
                .into(binding.imgDoctorDetail)
        }

        binding.btnConsult.setOnClickListener {
            val sellerPhone = "6281353941310"
            val message = "Halo, saya ingin konsultasi dengan dokter Anda"
            val whatsappUrl = "https://wa.me/$sellerPhone?text=${URLEncoder.encode(message, "UTF-8")}"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
            startActivity(browserIntent)


        }



    }
}
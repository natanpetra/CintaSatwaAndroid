package com.natan.klinik.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.natan.klinik.R
import com.natan.klinik.databinding.ActivityDetailEctoparasiteBinding
import com.natan.klinik.model.DoctorItem
import com.natan.klinik.model.Ectoparasite

class DetailDogEctoparasiteActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailEctoparasiteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEctoparasiteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setTitle("Detail Ectoparasite")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val Ectoparasite = intent.getSerializableExtra("ectoparasite") as? Ectoparasite

        if (Ectoparasite != null) {
            binding.txtGuideName.text = Ectoparasite.name
            binding.txtGuideDesc.text = Ectoparasite.symptoms
            binding.txtTreatment.text = Ectoparasite.treatment
            Glide.with(this@DetailDogEctoparasiteActivity)
                .load(Ectoparasite.image) // Pastikan ini sesuai dengan field gambar yang benar
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.img_doctor)
                .into(binding.imgDogGuide)
        }

    }
}
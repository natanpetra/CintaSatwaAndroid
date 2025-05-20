package com.natan.klinik.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.natan.klinik.R
import com.natan.klinik.databinding.ActivityDetailDogGuideBinding
import com.natan.klinik.model.DoctorItem
import com.natan.klinik.model.Guide

class DetailDogGuideActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailDogGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailDogGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setTitle("Detail Guide")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val guide = intent.getSerializableExtra("guide") as? Guide

        if (guide != null) {
            binding.txtGuideName.text = guide.title
            binding.txtGuideDesc.text = guide.content
            Glide.with(this@DetailDogGuideActivity)
                .load(guide.image) // Pastikan ini sesuai dengan field gambar yang benar
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.img_doctor)
                .into(binding.imgDogGuide)
        }

    }
}
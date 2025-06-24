package com.natan.klinik.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.natan.klinik.R
import com.natan.klinik.databinding.ActivityDetailEctoparasiteBinding
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

        val ectoparasite = intent.getSerializableExtra("ectoparasite") as? Ectoparasite

        if (ectoparasite != null) {
            binding.txtGuideName.text = ectoparasite.name
            binding.txtGuideDesc.text = ectoparasite.symptoms
            binding.txtTreatment.text = ectoparasite.treatment

            // ✅ FIXED: Use renamed helper function
            val imageUrl = ectoparasite.getFullImageUrl()
            Log.d("DetailEctoparasite", "Loading detail image: $imageUrl")

            // ✅ Check if imageUrl is not null before loading
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(this@DetailDogEctoparasiteActivity)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.img_dog_guide)
                    .error(R.drawable.img_dog_guide)
                    .into(binding.imgDogGuide)
            } else {
                Log.w("DetailEctoparasite", "Image URL is null or empty")
                // ✅ Set default image if no URL
                binding.imgDogGuide.setImageResource(R.drawable.img_dog_guide)
            }
        } else {
            Log.e("DetailEctoparasite", "Ectoparasite data is null")
        }
    }
}
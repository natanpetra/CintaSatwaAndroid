package com.natan.klinik.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.natan.klinik.R
import com.natan.klinik.databinding.ActivityBerandaBinding
import com.natan.klinik.databinding.ActivityConsultationBinding

class ConsultationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConsultationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConsultationBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}
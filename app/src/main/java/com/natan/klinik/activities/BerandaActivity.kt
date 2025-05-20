package com.natan.klinik.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.natan.klinik.R
import com.natan.klinik.databinding.ActivityBerandaBinding
import com.natan.klinik.fragment.HomeFragment
import com.natan.klinik.fragment.ProfileFragment
import com.pixplicity.easyprefs.library.Prefs

class BerandaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBerandaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBerandaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Prefs.getInt("is_scan", 0) == 0){
            binding.chat.visibility = View.GONE
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            if (item.getItemId() === R.id.home) {
                selectedFragment = HomeFragment()
            } else if (item.getItemId() === R.id.account) {
                selectedFragment = ProfileFragment()
            }

            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_container, selectedFragment).commit()
            }
            true
        }
        binding.chat.setOnClickListener { v ->
            if (Prefs.getString("token", "").equals("")) {
                startActivity(Intent(this, LoginActivity::class.java))
                return@setOnClickListener
            }
            startActivity(Intent(this, ScanActivity::class.java))
        }


        // Set default fragment saat aktivitas pertama kali dibuka
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, HomeFragment())
            .commit()

    }
}
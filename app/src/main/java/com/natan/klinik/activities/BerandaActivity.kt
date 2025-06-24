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

    // ✅ FEATURE FLAG - Control FAB Reservasi visibility
    private val SHOW_RESERVASI_FAB = false // ✅ Set false untuk hide FAB Reservasi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBerandaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupScanButton()
        setupReservasiFab() // ✅ Setup FAB visibility
        setupBottomNavigation()
        setDefaultFragment()
    }

    private fun setupScanButton() {
        if (Prefs.getInt("is_scan", 0) == 0) {
            binding.chat.visibility = View.GONE
        }

        binding.chat.setOnClickListener { v ->
            if (Prefs.getString("token", "").equals("")) {
                startActivity(Intent(this, LoginActivity::class.java))
                return@setOnClickListener
            }
            startActivity(Intent(this, ScanActivity::class.java))
        }
    }

    // ✅ NEW: Setup Reservasi FAB visibility
    private fun setupReservasiFab() {
        if (SHOW_RESERVASI_FAB) {
            // ✅ Show FAB and set click listener
            binding.fabReservasi.visibility = View.VISIBLE
            binding.fabReservasi.setOnClickListener {
                if (Prefs.getString("token", "").isEmpty()) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    return@setOnClickListener
                }
                startActivity(Intent(this, ReservasiActivity::class.java))
            }
        } else {
            // ✅ Hide FAB
            binding.fabReservasi.visibility = View.GONE
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            if (item.itemId === R.id.home) {
                selectedFragment = HomeFragment()
            } else if (item.itemId === R.id.account) {
                selectedFragment = ProfileFragment()
            }

            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_container, selectedFragment).commit()
            }
            true
        }
    }

    private fun setDefaultFragment() {
        // Set default fragment saat aktivitas pertama kali dibuka
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, HomeFragment())
            .commit()
    }
}
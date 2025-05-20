package com.natan.klinik.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.natan.klinik.fragment.RiwayatKonsultasiFragment
import com.natan.klinik.fragment.RiwayatObatFragment
import com.natan.klinik.fragment.RiwayatPemeriksaanFragment

class RiwayatPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RiwayatPemeriksaanFragment()
            1 -> RiwayatObatFragment()
            2 -> RiwayatKonsultasiFragment()
            else -> Fragment()
        }
    }
}

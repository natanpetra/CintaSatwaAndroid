package com.natan.klinik.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.natan.klinik.R
import com.natan.klinik.activities.LoginActivity
import com.natan.klinik.activities.ReservationHistoryActivity
import com.natan.klinik.databinding.FragmentProfileBinding
import com.pixplicity.easyprefs.library.Prefs

class ProfileFragment : Fragment() {
    lateinit var binding : FragmentProfileBinding

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        var name = Prefs.getString("name", "")
        var email = Prefs.getString("email", "")
        var role_id = Prefs.getInt("role_id", 0)
        var image = Prefs.getString("image", "")
        binding.tvNamaPasien.setText(name)
        binding.tvEmailPasien.setText(email)
        if (role_id == 2){
            binding.tvRolePasien.setText("Pasien")
        }else{
            binding.tvRolePasien.setText("Dokter/Admin")
        }
        Glide.with(context!!).load(image).placeholder(R.drawable.ic_account).into(binding.imgProfilPasien)

        binding.btnLogout.setOnClickListener {
            Prefs.clear()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        // Tambahkan listener untuk tombol riwayat reservasi
        binding.btnReservationHistory.setOnClickListener {
            startActivity(Intent(requireContext(), ReservationHistoryActivity::class.java))
        }

        return binding.root
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
package com.natan.klinik.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.natan.klinik.R
import com.natan.klinik.adapter.OrderHistoryAdapter
import com.natan.klinik.adapter.ScanHistoryAdapter
import com.natan.klinik.databinding.FragmentRiwayatObatBinding
import com.natan.klinik.model.OrderResponse
import com.natan.klinik.model.ScanResponse
import com.natan.klinik.network.RetrofitClient
import com.pixplicity.easyprefs.library.Prefs

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class RiwayatPemeriksaanFragment : Fragment() {
    lateinit var binding: FragmentRiwayatObatBinding
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var adapter: ScanHistoryAdapter

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
        binding = FragmentRiwayatObatBinding.inflate(inflater, container, false)


// Contoh pemanggilan API
        RetrofitClient.instance.getScanHistory(Prefs.getInt("user_id", 0)).enqueue(object : retrofit2.Callback<ScanResponse> {
            override fun onResponse(call: retrofit2.Call<ScanResponse>, response: retrofit2.Response<ScanResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        adapter = ScanHistoryAdapter(data.data, context!!)
                        binding.listObat.layoutManager = LinearLayoutManager(requireContext())
                        binding.listObat.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<ScanResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RiwayatPemeriksaanFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RiwayatPemeriksaanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
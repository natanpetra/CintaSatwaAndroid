package com.natan.klinik.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.natan.klinik.R
import com.natan.klinik.activities.ClinicActivity
import com.natan.klinik.activities.DoctorListActivity
import com.natan.klinik.activities.DogGuideActivity
import com.natan.klinik.activities.ObatActivity
import com.natan.klinik.activities.ProductListActivity
import com.natan.klinik.activities.ReservasiActivity
import com.natan.klinik.activities.RiwayatActivity
import com.natan.klinik.adapter.MainAdapter
import com.natan.klinik.databinding.FragmentHomeBinding
import com.natan.klinik.decoration.LayoutMarginDecoration
import com.natan.klinik.model.ModelMain
import com.natan.klinik.utils.Tools
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), MainAdapter.OnSelectData {
    private lateinit var binding: FragmentHomeBinding
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var gridMargin: LayoutMarginDecoration? = null
    private var mdlMainMenu: ModelMain? = null
    private var lsMainMenu: MutableList<ModelMain?> = ArrayList()
    private var hariIni: String? = null

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
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupDateTime()
        setMenu()

        return binding.root
    }

    private fun setupRecyclerView() {
        val mLayoutManager = GridLayoutManager(
            context,
            2,
            RecyclerView.VERTICAL,
            false
        )
        binding.rvMainMenu.layoutManager = mLayoutManager
        gridMargin = LayoutMarginDecoration(2, Tools.dp2px(requireContext(), 4f))
        binding.rvMainMenu.addItemDecoration(gridMargin!!)
        binding.rvMainMenu.setHasFixedSize(true)
    }

    private fun setupDateTime() {
        // Get Time Now
        val dateNow = Calendar.getInstance().time
        hariIni = DateFormat.format("EEEE", dateNow) as String
        binding.toString()
        setTodayDate()
    }

    private fun setTodayDate() {
        val date = Calendar.getInstance().time
        val tanggal = DateFormat.format("d MMMM yyyy", date) as String
        val formatFix = "$hariIni, $tanggal"
        binding.header.tvDate.text = formatFix
        binding.header.tvRiwayat.setOnClickListener {
            startActivity(Intent(requireContext(), RiwayatActivity::class.java))
        }
    }

    private fun setMenu() {
        mdlMainMenu = ModelMain("Klinik", R.drawable.img_clinic)
        lsMainMenu.add(mdlMainMenu)
        mdlMainMenu = ModelMain("Dog Care", R.drawable.img_dog_guide)
        lsMainMenu.add(mdlMainMenu)
        mdlMainMenu = ModelMain("Dokter", R.drawable.img_doctor)
        lsMainMenu.add(mdlMainMenu)
        mdlMainMenu = ModelMain("Produk", R.drawable.img_produk)
        lsMainMenu.add(mdlMainMenu)
        mdlMainMenu = ModelMain("Reservasi Online", R.drawable.img_reservasi)
        lsMainMenu.add(mdlMainMenu)

        val myAdapter = MainAdapter(lsMainMenu, this)
        binding.rvMainMenu.adapter = myAdapter
    }

    private fun call(phone: String) {
        val url = "https://api.whatsapp.com/send?phone=$phone"
        try {
            val pm: PackageManager = requireActivity().packageManager
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        } catch (e: PackageManager.NameNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    override fun onSelected(mdlMain: ModelMain?) {
        when (mdlMain?.txtName) {
            "Klinik" -> startActivity(Intent(requireContext(), ClinicActivity::class.java))
            "Dog Care" -> startActivity(Intent(requireContext(), DogGuideActivity::class.java))
            "Dokter" -> startActivity(Intent(requireContext(), DoctorListActivity::class.java))
            "Produk" -> startActivity(Intent(requireContext(), ProductListActivity::class.java))
            "Reservasi Online" -> startActivity(Intent(requireContext(), ReservasiActivity::class.java))
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
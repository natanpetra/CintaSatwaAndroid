package com.natan.klinik.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.natan.klinik.R
import com.natan.klinik.adapter.MainAdapter
import com.natan.klinik.decoration.LayoutMarginDecoration
import com.natan.klinik.model.ModelMain
import com.natan.klinik.utils.Tools
import java.util.Calendar

class MainActivity : Fragment(), MainAdapter.OnSelectData {
    private lateinit var rvMainMenu: RecyclerView
    private var gridMargin: LayoutMarginDecoration? = null
    private var mdlMainMenu: ModelMain? = null
    private var lsMainMenu: MutableList<ModelMain?> = ArrayList()
    private lateinit var tvToday: TextView
    private var hariIni: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_main, container, false)
        setupViews(view)
        setupRecyclerView()
        setupDateTime()
        setMenu()
        return view
    }

    private fun setupViews(view: View) {
        tvToday = view.findViewById(R.id.tvDate)
        rvMainMenu = view.findViewById(R.id.rvMainMenu)
    }

    private fun setupRecyclerView() {
        val mLayoutManager = GridLayoutManager(
            context,
            2,
            RecyclerView.VERTICAL,
            false
        )
        rvMainMenu.layoutManager = mLayoutManager
        gridMargin = LayoutMarginDecoration(2, Tools.dp2px(requireContext(), 4f))
        rvMainMenu.addItemDecoration(gridMargin!!)
        rvMainMenu.setHasFixedSize(true)
    }

    private fun setupDateTime() {
        // Get Time Now
        val dateNow = Calendar.getInstance().time
        hariIni = DateFormat.format("EEEE", dateNow) as String
        setTodayDate()
    }

    private fun setTodayDate() {
        val date = Calendar.getInstance().time
        val tanggal = DateFormat.format("d MMMM yyyy", date) as String
        val formatFix = "$hariIni, $tanggal"
        tvToday.text = formatFix
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
        rvMainMenu.adapter = myAdapter
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
}

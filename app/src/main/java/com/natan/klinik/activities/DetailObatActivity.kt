package com.natan.klinik.activities

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.natan.klinik.R
import com.natan.klinik.databinding.ActivityCheckoutBinding
import com.natan.klinik.databinding.ActivityDetailObatBinding
import com.natan.klinik.model.ProductItem
import com.natan.klinik.utils.SessionManager

class DetailObatActivity : AppCompatActivity() {
    private lateinit var obat: ProductItem
    private lateinit var session: SessionManager

    lateinit var binding: ActivityDetailObatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailObatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        val obat = intent.getParcelableExtra<ProductItem>("obat") ?: return

        val imgObat = findViewById<ImageView>(R.id.imgDetailObat)
        val tvNama = findViewById<TextView>(R.id.tvNamaObatDetail)
        val tvHarga = findViewById<TextView>(R.id.tvHargaObatDetail)
        Glide.with(this).load(obat.imageUrl).into(imgObat)
        tvNama.text = obat.name
        tvHarga.text = "Rp ${obat.price}"
        val btnTambah = findViewById<Button>(R.id.btnTambahKeranjang)
        val btnMinus = findViewById<Button>(R.id.btnMinus)
        val btnPlus = findViewById<Button>(R.id.btnPlus)
        val tvJumlah = findViewById<TextView>(R.id.tvJumlah)

        var jumlah = 1
        tvJumlah.text = jumlah.toString()

        btnMinus.setOnClickListener {
            if (jumlah > 1) {
                jumlah--
                tvJumlah.text = jumlah.toString()
            }
        }

        btnPlus.setOnClickListener {
            if (jumlah < 10) {
                jumlah++
                tvJumlah.text = jumlah.toString()
            }
        }

        btnTambah.setOnClickListener {
            session.tambahKeKeranjang(obat, jumlah)
            Toast.makeText(this, "Berhasil menambahkan $jumlah item ke keranjang", Toast.LENGTH_SHORT).show()
            finish()
        }


    }
}
package com.natan.klinik.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.natan.klinik.R

class ImageZoomDialog(
    context: Context,
    private val imageUrl: String
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.dialog_image_zoom)

        // Make dialog fullscreen
        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        setupViews()
    }

    private fun setupViews() {
        val imageView = findViewById<ImageView>(R.id.imgZoom)
        val btnClose = findViewById<ImageView>(R.id.btnClose)

        // Load image dengan Glide
        Glide.with(context)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.img_doctor) // placeholder jika error
            .into(imageView)

        // Close dialog when clicked
        btnClose.setOnClickListener { dismiss() }

        // Close dialog when background clicked
        findViewById<ViewGroup>(R.id.backgroundOverlay).setOnClickListener {
            dismiss()
        }
    }
}
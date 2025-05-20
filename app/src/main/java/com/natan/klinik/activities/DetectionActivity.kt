package com.natan.klinik.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.natan.klinik.R
import com.natan.klinik.utils.Detector
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import java.io.File
import java.io.FileOutputStream

class DetectionActivity : AppCompatActivity() {
    private lateinit var module: Module
    private lateinit var labels: List<String>
    private val imageRequestCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detection)

        module = LiteModuleLoader.load(assetFilePath(this, "best.torchscript.pt"))
        labels = assets.open("labels.txt").bufferedReader().readLines()

        val btnSelect = findViewById<Button>(R.id.btnSelect)
        btnSelect.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, imageRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == imageRequestCode && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            detect(bitmap)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun detect(bitmap: Bitmap) {
        val detector = Detector(module, labels)
        val results = detector.detect(bitmap)
        val resultBitmap = detector.drawResults(bitmap, results)
        findViewById<ImageView>(R.id.imageView).setImageBitmap(resultBitmap)
    }

    private fun assetFilePath(context: Context, assetName: String): String {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) return file.absolutePath

        context.assets.open(assetName).use { input ->
            FileOutputStream(file).use { output ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }
        return file.absolutePath
    }
}
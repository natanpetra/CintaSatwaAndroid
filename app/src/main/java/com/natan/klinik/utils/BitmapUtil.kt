package com.natan.klinik.utils

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.content.ContextCompat
import com.natan.klinik.R
import com.natan.klinik.utils.detectors.ObjectDetection
import java.io.File
import java.io.FileOutputStream
import java.security.SecureRandom
import kotlin.math.max

fun drawDetectionsOnBitmap(
    bitmap: Bitmap,
    detections: List<ObjectDetection>,
    imageWidth: Int,
    imageHeight: Int,
    context: Context,
): Bitmap {
    val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(mutableBitmap)

    val boxPaint = Paint().apply {
        strokeWidth = 8f
        style = Paint.Style.STROKE
    }

    val textBackgroundPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        textSize = 50f
    }

    val textPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        textSize = 50f
    }

    val bounds = Rect()

    // Calculate scale factors in case image view size is different
    val scaleX = mutableBitmap.width.toFloat() / imageWidth
    val scaleY = mutableBitmap.height.toFloat() / imageHeight

    for (result in detections) {
        val bbox = result.boundingBox

        boxPaint.color = Tools.classColors[result.category.label.lowercase()] ?: ContextCompat.getColor(context, R.color.bounding_box_color)

        val left = bbox.left * scaleX
        val top = bbox.top * scaleY
        val right = bbox.right * scaleX
        val bottom = bbox.bottom * scaleY

        val rect = RectF(left, top, right, bottom)
        canvas.drawRect(rect, boxPaint)

        val labelText = "${result.category.label} ${"%.2f".format(result.category.confidence)}"

        textBackgroundPaint.getTextBounds(labelText, 0, labelText.length, bounds)
        val textWidth = bounds.width()
        val textHeight = bounds.height()

        canvas.drawRect(
            left,
            top,
            left + textWidth + 8,
            top + textHeight + 8,
            textBackgroundPaint
        )

        canvas.drawText(labelText, left, top + bounds.height(), textPaint)
    }

    return mutableBitmap
}

fun Bitmap.saveToCacheFile(context: Context, filename: String): File? {
    return try {
        val cacheDir = context.cacheDir

        val file = File(cacheDir, filename)

        val fos = FileOutputStream(file)
        this.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()

        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun createFile(application: Application): File {
    return File(application.cacheDir, "${SecureRandom.getSeed(100)}.jpg")
}

fun File.toBitmap(): Bitmap {
    return BitmapFactory.decodeFile(this.path)
}


fun Bitmap.rotateBitmap(isBackCamera: Boolean = false): Bitmap {
    val matrix = Matrix()
    return if (isBackCamera) {
        matrix.postRotate(90f)
        Bitmap.createBitmap(
            this,
            0,
            0,
            this.width,
            this.height,
            matrix,
            true
        )
    } else {
        matrix.postRotate(-90f)
        matrix.postScale(-1f, 1f, this.width / 2f, this.height / 2f)
        Bitmap.createBitmap(
            this,
            0,
            0,
            this.width,
            this.height,
            matrix,
            true
        )
    }
}

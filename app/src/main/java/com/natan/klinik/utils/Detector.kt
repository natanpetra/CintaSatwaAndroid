package com.natan.klinik.utils

import android.graphics.*
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils

class Detector(private val module: Module, private val labels: List<String>) {

    data class Detection(val rect: RectF, val label: String, val confidence: Float)

    fun detect(bitmap: Bitmap): List<Detection> {
        val resized = Bitmap.createScaledBitmap(bitmap, 640, 640, true)

        val input = TensorImageUtils.bitmapToFloat32Tensor(
            resized,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )

        val output = module.forward(IValue.from(input)).toTensor()
        val outputs = output.dataAsFloatArray

        val detections = mutableListOf<Detection>()
        for (i in outputs.indices step 6) {
            val x = outputs[i]
            val y = outputs[i + 1]
            val w = outputs[i + 2]
            val h = outputs[i + 3]
            val conf = outputs[i + 4]
            val classId = outputs[i + 5].toInt()

            if (conf > 0.5f) {
                val left = x - w / 2
                val top = y - h / 2
                val rect = RectF(left, top, left + w, top + h)
                val label = labels.getOrElse(classId) { "?" }
                detections.add(Detection(rect, label, conf))
            }
        }
        return detections
    }

    fun drawResults(bitmap: Bitmap, detections: List<Detection>): Bitmap {
        val mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutable)
        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        val textPaint = Paint().apply {
            color = Color.YELLOW
            textSize = 40f
            style = Paint.Style.FILL
        }

        for (d in detections) {
            canvas.drawRect(d.rect, paint)
            canvas.drawText("${d.label} ${(d.confidence * 100).toInt()}%", d.rect.left, d.rect.top - 10, textPaint)
        }

        return mutable
    }
}
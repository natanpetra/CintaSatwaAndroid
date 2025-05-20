package com.natan.klinik.utils.detectors

import android.graphics.Bitmap
import android.graphics.RectF
import org.tensorflow.lite.support.image.TensorImage

class Category (
    val label: String,
    val confidence: Float
)

class ObjectDetection(
    val boundingBox: RectF,
    val category: Category
)

class DetectionResult(
    val image: Bitmap,
    val detections: List<ObjectDetection>,
    var info: Any?=null
)

enum class ImageSource {
    CAMERA,
    GALLERY
}

interface ObjectDetector {
    fun detect(image: TensorImage, imageRotation: Int, source: ImageSource): DetectionResult
}





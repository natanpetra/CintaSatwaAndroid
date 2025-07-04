package com.natan.klinik.utils.detectors

import android.content.Context
import android.graphics.RectF
import com.ultralytics.yolo.ImageProcessing
import com.ultralytics.yolo.models.LocalYoloModel
import com.ultralytics.yolo.predict.detect.DetectedObject
import com.ultralytics.yolo.predict.detect.TfliteDetector
import org.tensorflow.lite.support.image.TensorImage



class YoloDetector(
    var confidenceThreshold: Float = 0.3f,
    var iouThreshold: Float = 0.3f,
    var numThreads: Int = 2,
    var maxResults: Int = 3,
    var currentDelegate: Int = 0,
    var currentModel: Int = 0,
    val context: Context
): ObjectDetector {

    private var yolo: TfliteDetector = TfliteDetector(context)
    private var ip: ImageProcessing

    init {
        yolo.setIouThreshold(iouThreshold)
        yolo.setConfidenceThreshold(confidenceThreshold)
//        val modelPath = "v8real.tflite"
        val modelPath = "v7000.tflite"
        val metadataPath = "metadata.yaml"

        val config = LocalYoloModel(
            "detect",
            "tflite",
            modelPath,
            metadataPath,
        )

        val useGPU = currentDelegate == 0
        yolo.loadModel(
            config,
            useGPU
        )

        ip = ImageProcessing()
    }

    override fun detect(image: TensorImage, imageRotation: Int, source: ImageSource): DetectionResult {

        val bitmap = image.bitmap

        val ppImage = yolo.preprocess(bitmap)
        val results = yolo.predict(ppImage)

        val detections = ArrayList<ObjectDetection>()

        // ASPECT_RATIO = 4:3
        // => imgW = imgH * 3/4
        val imgH: Int
        val imgW: Int
        when (source) {
            ImageSource.CAMERA -> {
                if (imageRotation == 90 || imageRotation == 270) {
                    imgH = ppImage.height
                    imgW = imgH * 3 / 4
                }
                else {
                    imgW = ppImage.width
                    imgH = imgW * 3 / 4
                }
            }
            ImageSource.GALLERY -> {
                imgW = ppImage.width
                imgH = ppImage.height
            }

            ImageSource.CAMERA_SAVED -> {
                imgW = ppImage.width
                imgH = ppImage.height
            }
        }

        for (result: DetectedObject in results) {
            val category = Category(
                result.label,
                result.confidence,
            )
            val yoloBox = result.boundingBox

            val left = yoloBox.left * imgW
            val top = yoloBox.top * imgH
            val right = yoloBox.right * imgW
            val bottom = yoloBox.bottom * imgH

            val bbox = RectF(
                left,
                top,
                right,
                bottom
            )
            val detection = ObjectDetection(
                bbox,
                category
            )
            detections.add(detection)
        }

        val ret = DetectionResult(ppImage, detections)
        ret.info = yolo.stats
        return ret

    }
}
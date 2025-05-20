/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.natan.klinik.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import com.natan.klinik.utils.detectors.ImageSource
import com.natan.klinik.utils.detectors.ObjectDetection
import com.natan.klinik.utils.detectors.ObjectDetector
import com.natan.klinik.utils.detectors.YoloDetector
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op

class ObjectDetectorHelper(
    var threshold: Float = 0.3f,
    var numThreads: Int = 2,
    var maxResults: Int = 3,
    var currentDelegate: Int = 0,
    var currentModel: Int = MODEL_YOLO,
    val context: Context,
    val objectDetectorListener: DetectorListener?,
) {

    // For this example this needs to be a var so it can be reset on changes. If the ObjectDetector
    // will not change, a lazy val would be preferable.
    private var objectDetector: ObjectDetector? = null

    init {
        setupObjectDetector()
    }

    fun clearObjectDetector() {
        objectDetector = null
    }

    private fun setupObjectDetector() {

        try {

            objectDetector = YoloDetector(
                threshold,
                0.3f,
                numThreads,
                maxResults,
                currentDelegate,
                currentModel,
                context
            )
        } catch (e: Exception) {

            objectDetectorListener?.onError(e.toString())

        }
    }


    fun detect(image: Bitmap, imageRotation: Int, source: ImageSource) {

        if (objectDetector == null) {
            setupObjectDetector()
        }

        val imageProcessor =
            ImageProcessor.Builder()
                .add(Rot90Op(-imageRotation / 90))
                .build()

        // Preprocess the image and convert it into a TensorImage for detection.
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))

        var inferenceTime = SystemClock.uptimeMillis()

        val results = objectDetector?.detect(tensorImage, imageRotation, source)

        inferenceTime = SystemClock.uptimeMillis() - inferenceTime

        if (results != null) {
            objectDetectorListener?.onResults(
                results.detections,
                inferenceTime,
                results.image.height,
                results.image.width,
                source
            )
        }
    }

    interface DetectorListener {
        fun onError(error: String)
        fun onResults(
            results: List<ObjectDetection>,
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int,
            source: ImageSource,
        )
    }

    companion object {
        const val MODEL_YOLO = 1
    }
}

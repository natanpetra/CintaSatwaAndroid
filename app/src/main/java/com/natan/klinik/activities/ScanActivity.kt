package com.natan.klinik.activities

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageSavedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.exifinterface.media.ExifInterface
import com.natan.klinik.R
import com.natan.klinik.databinding.ActivityScanBinding
import com.natan.klinik.model.SubmitScanResponse
import com.natan.klinik.network.RetrofitClient
import com.natan.klinik.utils.ObjectDetectorHelper
import com.natan.klinik.utils.createFile
import com.natan.klinik.utils.detectors.ImageSource
import com.natan.klinik.utils.detectors.ObjectDetection
import com.natan.klinik.utils.drawDetectionsOnBitmap
import com.natan.klinik.utils.rotateBitmap
import com.natan.klinik.utils.saveToCacheFile
import com.natan.klinik.utils.toBitmap
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.tensorflow.lite.Interpreter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileInputStream
import java.io.OutputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanActivity : AppCompatActivity(), ObjectDetectorHelper.DetectorListener {

    private lateinit var binding: ActivityScanBinding
    private lateinit var tflite: Interpreter
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    private lateinit var bitmapBufferGallery: Bitmap
    private lateinit var bitmapBufferCamera: Bitmap
    private lateinit var imageView: ImageView
    private lateinit var selectButton: Button
    private var lastDetectedBitmap: Bitmap? = null
    private var pickedBitmapFromGallery: Bitmap? = null
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null

    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageView = findViewById(R.id.imageView)
        selectButton = findViewById(R.id.selectButton)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        }

        tflite = Interpreter(loadModelFile())
        objectDetectorHelper = ObjectDetectorHelper(
            context = this,
            objectDetectorListener = this
        )
        cameraExecutor = Executors.newSingleThreadExecutor()

        selectButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.viewFinder.post {
            // Set up the camera and its use cases
            setUpCamera()
        }
        binding.btnClose.setOnClickListener {
            pickedBitmapFromGallery = null
            binding.imageView.setImageDrawable(null)
            binding.btnClose.visibility = View.GONE
        }

        binding.btnSave.setOnClickListener {
            pickedBitmapFromGallery?.let {
                saveBitmapToStorage(it)
                Toast.makeText(this, "Tersimpan di galery", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(this, "TAKING..", Toast.LENGTH_SHORT).show()
                takePhoto()
            }
        }

        binding.btnUpload.setOnClickListener {
            uploadImageDetected(this)
        }
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()

                // Build and bind the camera use cases
                bindCameraUseCases()
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    private fun bindCameraUseCases() {
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector - makes assumption that we're only using the back camera
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview =
            Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .build()

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_RGBA_8888)
                //.setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(cameraExecutor) { image ->
                        if (!::bitmapBufferCamera.isInitialized) {
                            // The image rotation and RGB image buffer are initialized only once
                            // the analyzer has started running
                            bitmapBufferCamera = createBitmap(image.width, image.height)
                        }

                        detectObjects(image)
                    }
                }
        imageCapture = ImageCapture.Builder().build()
        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer, imageCapture)

            // Attach the viewfinder's surface provider to preview use case
            preview?.surfaceProvider = binding.viewFinder.surfaceProvider
        } catch (exc: Exception) {
//            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun detectObjects(image: ImageProxy) {
        image.use {
            bitmapBufferCamera.copyPixelsFromBuffer(image.planes[0].buffer)
        }

        val imageRotation = image.imageInfo.rotationDegrees
        // Pass Bitmap and rotation to the object detector helper for processing and detection
        objectDetectorHelper.detect(bitmapBufferCamera, imageRotation, ImageSource.CAMERA)
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = assets.openFd("best_float32_1.tflite")
//        val fileDescriptor = assets.openFd("v11real.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                val inputStream = contentResolver.openInputStream(uri)
                bitmapBufferGallery = BitmapFactory.decodeStream(inputStream)
                val imageRotation = getImageRotationFromUri(uri)
                if (::bitmapBufferGallery.isInitialized) {
                    objectDetectorHelper.detect(bitmapBufferGallery, imageRotation, ImageSource.GALLERY)
                }
            }
        }
    }
//get image rotation Exit interface
    private fun getImageRotationFromUri(imageUri: Uri): Int {
        val inputStream = contentResolver.openInputStream(imageUri)
        val exif = inputStream?.let { ExifInterface(it) }
        val orientation = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }

    private fun saveBitmapToStorage(bitmap: Bitmap): Uri? {
        val filename = "detected_${System.currentTimeMillis()}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Detections")
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        uri?.let {
            val outputStream: OutputStream? = contentResolver.openOutputStream(it)
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
                Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show()
            }
        }

        return uri
    }

    private fun updateControlsUi() {
        objectDetectorHelper.clearObjectDetector()
        binding.overlay.clear()
    }

    override fun onError(error: String) {
        runOnUiThread {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResults(
        results: List<ObjectDetection>,
        inferenceTime: Long,
        imageRotation: Int,
        image: Bitmap,
        imageHeight: Int,
        imageWidth: Int,
        source: ImageSource,
    ) {
        runOnUiThread {
            when (source) {
                ImageSource.CAMERA -> {
                    binding.overlay.setResults(
                        results,
                        imageHeight,
                        imageWidth,
                    )
                    binding.overlay.invalidate()
                }
                ImageSource.GALLERY -> {
                    val bitmap = drawDetectionsOnBitmap(
                        bitmapBufferGallery,
                        results,
                        imageWidth,
                        imageHeight,
                        this
                    )
                    pickedBitmapFromGallery = bitmap
                    imageView.setImageBitmap(bitmap)
                    binding.btnClose.visibility = View.VISIBLE
                }
                ImageSource.CAMERA_SAVED -> {
                    lastDetectedBitmap = drawDetectionsOnBitmap(
                        image,
                        results,
                        imageWidth,
                        imageHeight,
                        this
                    )
                    lastDetectedBitmap?.let {
                        saveBitmapToStorage(it)
                        Toast.makeText(this, "Tersimpan di galery", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation = binding.viewFinder.display.rotation
    }

    fun uploadImageDetected(ctx: Context) {
        val imageFile = pickedBitmapFromGallery?.saveToCacheFile(ctx, "detected_image.jpg")
            ?: lastDetectedBitmap?.saveToCacheFile(ctx, "detected_image.jpg")
            ?: return

        val reqFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("photo", imageFile.name, reqFile)
        val userPart  = Prefs.getInt("user_id", 0).toString().toRequestBody("text/plain".toMediaType())   // ← new

        RetrofitClient.instance.submitScan(part, userPart).enqueue(object : Callback<SubmitScanResponse> {
            override fun onResponse(
                call: Call<SubmitScanResponse>,
                response: Response<SubmitScanResponse>,
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(ctx, "Upload berhasil", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(ctx, "Upload gagal: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SubmitScanResponse>, t: Throwable) {
                Toast.makeText(ctx, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createFile(application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val imageBitmap = photoFile.toBitmap()
                    val rotatedBitmap = imageBitmap.rotateBitmap(
                        cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                    )
                    objectDetectorHelper.detect(rotatedBitmap, 0, ImageSource.CAMERA_SAVED)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@ScanActivity,
                        exception.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}
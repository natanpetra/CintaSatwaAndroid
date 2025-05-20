package com.natan.klinik.activities

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
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
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.natan.klinik.R
import com.natan.klinik.databinding.ActivityScanBinding
import com.natan.klinik.model.SubmitScanResponse
import com.natan.klinik.network.RetrofitClient
import com.natan.klinik.utils.ObjectDetectorHelper
import com.natan.klinik.utils.Tools
import com.natan.klinik.utils.detectors.ImageSource
import com.natan.klinik.utils.detectors.ObjectDetection
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
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
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

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
            binding.imageView.setImageDrawable(null)
            binding.btnClose.visibility = View.GONE
        }

        binding.btnSave.setOnClickListener {
            captureAndSave(binding.root, this)?.let {
                Toast.makeText(this, "Disimpan ke galeri", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnUpload.setOnClickListener {
            uploadScreenshot(binding.root, this)
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
                            bitmapBufferCamera = Bitmap.createBitmap(
                                image.width,
                                image.height,
                                Bitmap.Config.ARGB_8888
                            )
                        }

                        detectObjects(image)
                    }
                }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)

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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val inputStream = contentResolver.openInputStream(uri)
                bitmapBufferGallery = BitmapFactory.decodeStream(inputStream)
//                bitmap?.let {
//                    val resized = Bitmap.createScaledBitmap(it, inputSize, inputSize, true)
//                    val resultBitmap = detectObjects(resized, it)
//                    imageView.setImageBitmap(resultBitmap)
//                }
                val imageRotation = getImageRotationFromUri(uri)
                if (::bitmapBufferGallery.isInitialized) {
                    objectDetectorHelper.detect(bitmapBufferGallery, imageRotation, ImageSource.GALLERY)
                }
            }
        }
    }

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

    override fun onError(error: String) {
        runOnUiThread {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateControlsUi() {
        objectDetectorHelper.clearObjectDetector()
        binding.overlay.clear()
    }

    override fun onResults(
        results: List<ObjectDetection>,
        inferenceTime: Long,
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
                        imageWidth
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
                    imageView.setImageBitmap(bitmap)
                    binding.btnClose.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun drawDetectionsOnBitmap(
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation = binding.viewFinder.display.rotation
    }

    fun captureAndSave(view: View, context: Context): File? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        val filename = "screenshot_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/KlinikScan")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val contentResolver = context.contentResolver
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            contentResolver.openOutputStream(uri).use {
                if (it != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
            }
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(uri, contentValues, null, null)

            // Simpan file ke cache untuk dikirim via Retrofit
            val file = File(context.cacheDir, filename)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            return file
        }

        return null
    }

    fun uploadScreenshot(view: View, ctx: Context) {
        val imgFile = captureAndSave(view, ctx) ?: run {
            Toast.makeText(ctx, "Gagal ambil screenshot", Toast.LENGTH_SHORT).show(); return
        }

        val reqFile = imgFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("photo", imgFile.name, reqFile)
        val userPart  = Prefs.getInt("user_id", 0).toString().toRequestBody("text/plain".toMediaType())   // ← new

        RetrofitClient.instance.submitScan(part, userPart).enqueue(object : Callback<SubmitScanResponse> {
            override fun onResponse(
                call: Call<SubmitScanResponse>,
                response: Response<SubmitScanResponse>
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



}
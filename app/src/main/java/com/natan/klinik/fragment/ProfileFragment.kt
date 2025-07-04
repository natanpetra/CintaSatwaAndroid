package com.natan.klinik.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.natan.klinik.R
import com.natan.klinik.activities.LoginActivity
import com.natan.klinik.activities.ReservationHistoryActivity
import com.natan.klinik.databinding.FragmentProfileBinding
import com.pixplicity.easyprefs.library.Prefs
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import com.natan.klinik.model.UploadResponse
import com.natan.klinik.network.RetrofitClient
import com.natan.klinik.utils.FileUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.imgProfilPasien.setImageURI(it)
                val userId = Prefs.getInt("user_id", 0)
                uploadThumbnail(userId, it, requireContext())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupProfileData()
        setupClickListeners()

        return binding.root
    }

    private fun setupProfileData() {
        val name = Prefs.getString("name", "")
        val email = Prefs.getString("email", "")
        val phone = Prefs.getString("phone", "")
        val roleId = Prefs.getInt("role_id", 0)
        val image = Prefs.getString("image", "")

        // ✅ FIX: Set data dengan proper handling
        binding.tvNamaPasien.text = name.ifEmpty { "Nama tidak tersedia" }
        binding.tvEmailPasien.text = email.ifEmpty { "Email tidak tersedia" }

        // ✅ FIX: Tampilkan nomor HP yang sesuai dengan registrasi
        binding.tvPhonePasien.text = formatPhoneNumber(phone)

        // Set role
        binding.tvRolePasien.text = if (roleId == 2) "Pasien" else "Dokter/Admin"

        // Load profile image
        Glide.with(requireContext())
            .load(image)
            .placeholder(R.drawable.ic_account)
            .error(R.drawable.ic_account)
            .into(binding.imgProfilPasien)

        // ✅ Debug log untuk memastikan data yang tersimpan
        Log.d("ProfileFragment", "Name: $name")
        Log.d("ProfileFragment", "Email: $email")
        Log.d("ProfileFragment", "Phone: $phone")
        Log.d("ProfileFragment", "Role ID: $roleId")
    }

    // ✅ FIX: Format nomor HP dengan benar
    private fun formatPhoneNumber(phone: String): String {
        if (phone.isEmpty()) return "Nomor HP tidak tersedia"

        // Jika phone masih dalam format Long/Int dari server, konversi dulu
        val phoneStr = phone.toString()

        // Jika sudah dalam format yang benar, return langsung
        if (phoneStr.startsWith("0") || phoneStr.startsWith("+62")) {
            return phoneStr
        }

        // Jika hanya angka tanpa prefix, tambahkan 0 di depan
        return if (phoneStr.matches(Regex("^[0-9]+$"))) {
            "0$phoneStr"
        } else {
            phoneStr
        }
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.btnReservationHistory.setOnClickListener {
            startActivity(Intent(requireContext(), ReservationHistoryActivity::class.java))
        }

        binding.imgProfilPasien.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun logout() {
        // ✅ Clear all saved data
        Prefs.clear()

        // Navigate to login
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    fun uploadThumbnail(userId: Int, fileUri: Uri, context: Context) {
        val file = java.io.File(FileUtils.getPath(context, fileUri))
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("thumbnail", file.name, requestFile)
        val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId.toString())

        val call = RetrofitClient.instance.uploadThumbnail(userIdBody, body)
        call.enqueue(object : retrofit2.Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: retrofit2.Response<UploadResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("Upload", "Success: ${response.body()?.image}")
                    Prefs.putString("image", response.body()?.image)
                } else {
                    Log.e("Upload", "Failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Log.e("Upload", "Error: ${t.message}")
            }
        })
    }

    private fun requestPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.READ_MEDIA_IMAGES
            if (requireContext().checkSelfPermission(permission) ==
                PackageManager.PERMISSION_GRANTED) {
                pickImageLauncher.launch("image/*")
            } else {
                requestPermissions(arrayOf(permission), 1001)
            }
        } else {
            pickImageLauncher.launch("image/*")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageLauncher.launch("image/*")
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
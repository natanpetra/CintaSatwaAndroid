package com.natan.klinik.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.natan.klinik.R
import com.natan.klinik.model.Profile
import com.natan.klinik.network.RetrofitClient
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var registerBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Ambil komponen dari layout
        nameInput = findViewById(R.id.et_name)
        emailInput = findViewById(R.id.et_email)
        phoneInput = findViewById(R.id.et_phone)
        passwordInput = findViewById(R.id.et_password)

        registerBtn = findViewById(R.id.btnRegister)

        registerBtn.setOnClickListener {
            if (validateForm()) {
                val name = nameInput.text.toString().trim()
                val email = emailInput.text.toString().trim()
                val phone = phoneInput.text.toString().trim()
                val password = passwordInput.text.toString().trim()
                // Ganti dengan proses register API / local logic
                RetrofitClient.instance.register(email, name, phone, password).enqueue(object : retrofit2.Callback<Profile> {
                    override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                        if (response.isSuccessful) {
                            val profile = response.body()
                            if (profile != null) {
                                Prefs.putString("token", profile.tokenApi)
                                Prefs.putString("name", profile.name)
                                Prefs.putString("email", profile.email)
                                Prefs.putInt("role_id", profile.roleId!!)
                                Prefs.putString("image", profile.imageUrl)
                                Toast.makeText(this@RegisterActivity, "Register Berhasil, mohon login terlebih dahulu", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        } else {
                            Toast.makeText(this@RegisterActivity, "Register Gagal", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Profile>, t: Throwable) {
                        Log.e("LoginActivity", "Error: ${t.message}")
                        Toast.makeText(this@RegisterActivity, "Register Gagal", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        // Validasi nama
        if (name.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
            valid = false
        }

        // Validasi email
        if (email.isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
            valid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            valid = false
        }

        // Validasi nomor telepon
        if (phone.isEmpty()) {
            Toast.makeText(this, "Phone number is required", Toast.LENGTH_SHORT).show()
            valid = false
        } else if (!phone.matches(Regex("^\\d{7,12}$"))) {
            Toast.makeText(this, "Phone must be 7-12 digits", Toast.LENGTH_SHORT).show()
            valid = false
        }

        // Validasi password
        if (password.isEmpty()) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
            valid = false
        } else if (password.length < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
            valid = false
        }

        return valid
    }
}
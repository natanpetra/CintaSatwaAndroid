package com.natan.klinik.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.natan.klinik.R
import com.natan.klinik.model.Profile
import com.natan.klinik.network.RetrofitClient
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.Callback
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtEmail = findViewById(R.id.et_username)
        edtPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        tvRegister = findViewById(R.id.tv_register)

        if (!Prefs.getString("token").equals("")) {
            startActivity(Intent(this, BerandaActivity::class.java))
            finish()
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        if (!Prefs.getString("token").equals("")) {
            startActivity(Intent(this, BerandaActivity::class.java))
            finish()
        }

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password wajib diisi!", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        RetrofitClient.instance.login(email, password).enqueue(object : retrofit2.Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                    if (response.isSuccessful) {
                        val profile = response.body()
                        if (profile != null) {
                            Prefs.putString("token", profile.tokenApi)
                            Prefs.putInt("user_id", profile.userId!!)
                            Prefs.putString("name", profile.name)
                            Prefs.putString("email", profile.email)
                            Prefs.putInt("role_id", profile.roleId!!)
                            Prefs.putString("image", profile.imageUrl)
                            Prefs.putInt("is_scan", profile.isScan!!)
                            Toast.makeText(this@LoginActivity, "Login Berhasil", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, BerandaActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Login Gagal", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    Log.e("LoginActivity", "Error: ${t.message}")
                    Toast.makeText(this@LoginActivity, "Login Gagal", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

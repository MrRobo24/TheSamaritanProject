package com.identity_crisis.thesamaritans.activity.launcher

import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var edEmail: EditText
    lateinit var edPassword: EditText
    lateinit var btnLogin: Button
    lateinit var txtRegister: TextView
    lateinit var sharedPreferences: SharedPreferences
    var uid = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edEmail = findViewById(R.id.edEmail)
        edPassword = findViewById(R.id.edPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtRegister = findViewById(R.id.txtRegister)

        sharedPreferences =
            getSharedPreferences(getString(R.string.sharedPreference), Context.MODE_PRIVATE)

        btnLogin.setOnClickListener {

            val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

            if (isConnected) {
                Log.d("LoginActivity","Internet available")
                login()
            } else {
                Log.d("LoginActivity","Internet not available")
                alertNoNet()
            }
        }

        txtRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun login() {
        val email = edEmail.text.toString().trim()
        val password = edPassword.text.toString().trim()

        if (email.isEmpty()) {
            edEmail.error = "Mandatory field"
            return
        } else if (password.isEmpty()) {
            edPassword.error = "Mandatory field"
            return
        }

        progressBar.visibility= View.VISIBLE

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnFailureListener {
                progressBar.visibility=View.INVISIBLE
                Toast.makeText(this@LoginActivity,"Login error!!", Toast.LENGTH_SHORT).show()
                Log.d("Main", "Cannot Login user with uid ${it.message}")
            }
            .addOnSuccessListener {
                progressBar.visibility=View.INVISIBLE
//                if (!it.isSuccessful)
//                    return@addOnCompleteListener

                edEmail.setText("")
                edPassword.setText("")

                uid = FirebaseAuth.getInstance().uid.toString()
                sharedPreferences.edit().putString("UID", uid).apply()

                Log.d("Main", "Successfully Login User")
                Toast.makeText(this@LoginActivity, "Successfully Login!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                progressBar.visibility=View.INVISIBLE
                startActivity(intent)

            }

    }
    fun alertNoNet() {
        val dialog = AlertDialog.Builder(this@LoginActivity)
        dialog.setTitle("Error")
        dialog.setMessage("Internet Connection is Not Found")
        dialog.setPositiveButton("Open Settings") { _, _ ->
            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(settingsIntent)
            ActivityCompat.finishAffinity(this@LoginActivity)
        }
        dialog.setNegativeButton("Exit") { _, _ ->
            ActivityCompat.finishAffinity(this@LoginActivity)
        }

        dialog.create()
        dialog.show()
    }
}
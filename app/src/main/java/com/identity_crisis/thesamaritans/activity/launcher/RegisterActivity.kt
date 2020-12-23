package com.identity_crisis.thesamaritans.activity.launcher

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.model.User
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    lateinit var edUsername: EditText
    lateinit var edEmail: EditText
    lateinit var edPassword: EditText
    lateinit var btnRegister: Button
    lateinit var txtLogin: TextView
    lateinit var img_profile: CircleImageView
    lateinit var sharedPreferences: SharedPreferences
    var selectedPhotoUri: Uri?=null
    var username=""
    var email=""
    var uid=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        edUsername=findViewById(R.id.edUsername)
        edEmail=findViewById(R.id.edEmail)
        edPassword=findViewById(R.id.edPassword)
        btnRegister=findViewById(R.id.btnRegister)
        txtLogin=findViewById(R.id.txtLogin)
        img_profile=findViewById(R.id.img_profile)

        sharedPreferences=getSharedPreferences(getString(R.string.sharedPreference), Context.MODE_PRIVATE)

//        img_profile.setOnClickListener {
//            Log.d("Main","Selecting image")
//            val intent= Intent(Intent.ACTION_PICK)
//            intent.type="image/*"
//            startActivityForResult(intent,0)
//        }

        btnRegister.setOnClickListener{
            val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

            if (isConnected) {
                Log.d("RegisterActivity","Internet available")
                register()
            } else {
                Log.d("RegisterActivity","Internet not available")
                alertNoNet()
            }
        }

        txtLogin.setOnClickListener {
            val intent= Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun alertNoNet() {
        val dialog = AlertDialog.Builder(this@RegisterActivity)
        dialog.setTitle("Error")
        dialog.setMessage("Internet Connection is Not Found")
        dialog.setPositiveButton("Open Settings") { _, _ ->
            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(settingsIntent)
            ActivityCompat.finishAffinity(this@RegisterActivity)
        }
        dialog.setNegativeButton("Exit") { _, _ ->
            ActivityCompat.finishAffinity(this@RegisterActivity)
        }

        dialog.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==0 && resultCode== Activity.RESULT_OK && data!=null){
            Log.d("Main","Image was selected")

            selectedPhotoUri=data.data
//            if (android.os.Build.VERSION.SDK_INT >= 29){
//                // To handle deprication use
//                bitmap=ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver,selectedPhotoUri))
//            } else{
//                // Use older version
            val bitmap= MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
//            }

            img_profile.setImageBitmap(bitmap)
//            img_profile.alpha=0f

//            val bitmapDrawable=BitmapDrawable(applicationContext.resources,
//            Bitmap.createBitmap(bitmap!!))
//
//            img_profile.background = bitmapDrawable
        }
    }

    private fun register(){
        username=edUsername.text.toString().trim()
        email=edEmail.text.toString().trim()
        val password=edPassword.text.toString().trim()

        if(username.isEmpty()){
            edUsername.error = "Mandatory field"
            return
        }else if(email.isEmpty()) {
            edEmail.error = "Mandatory field"
            return
        }else if(password.isEmpty()){
            edPassword.error = "Mandatory field"
            return
        }

        progressBar.visibility= View.VISIBLE
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
//                if(!it.isSuccessful)
//                    return@addOnCompleteListener

                Log.d("Main","Successfully Registered user")
                Toast.makeText(this@RegisterActivity,"Successfully Registered!", Toast.LENGTH_SHORT).show()
                uid= FirebaseAuth.getInstance().uid.toString()
                saveDataToDatabase()
                edUsername.setText("")
                edEmail.setText("")
                edPassword.setText("")
                sharedPreferences.edit().putString("UID",uid).apply()
                val intent= Intent(this@RegisterActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                progressBar.visibility= View.INVISIBLE

                startActivity(intent)
//                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                progressBar.visibility= View.INVISIBLE
                Toast.makeText(this@RegisterActivity,"Registration error!!",Toast.LENGTH_SHORT).show()
                Log.d("Main","Cannot Register!!")
            }
    }

    private fun saveDataToDatabase(){
        val ref= FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user= User(
            uid,
            username,
            email,
            ""
        )

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Main","Uploaded data successfully to Firebase Database")
            }
            .addOnFailureListener {
                Log.d("Main","Failed to upload data to Firebase Database ${it.message}")
            }
    }

    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri==null){
            saveImageToFirebaseDatabase("")
            return
        }

        val filename= UUID.randomUUID().toString()
        val ref= FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Main","Uploaded image successfully with path ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("Main","Image Url: $it")

                    saveImageToFirebaseDatabase(it.toString())
                }
            }
    }

    private fun saveImageToFirebaseDatabase(profileImageUrl: String) {
        val uid=FirebaseAuth.getInstance().uid
        val ref= FirebaseDatabase.getInstance().getReference("/users/$uid")

//        val user= User(
//            uid.toString(),
//            username,
//            email,
//            profileImageUrl
//        )

//        ref.setValue(user)
//            .addOnSuccessListener {
//                Log.d("Main","Uploaded data successfully to Firebase Database")
//            }
    }
}
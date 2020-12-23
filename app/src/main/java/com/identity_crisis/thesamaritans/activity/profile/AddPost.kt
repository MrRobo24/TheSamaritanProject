package com.identity_crisis.thesamaritans.activity.profile

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.activity.launcher.MainActivity
import com.identity_crisis.thesamaritans.model.Data
import kotlinx.android.synthetic.main.addpost_dialog.*
import kotlinx.android.synthetic.main.progressdialog.*
import java.util.*


class AddPost : AppCompatActivity() {
    private val GALLERY_REQUEST_CODE = 123
    var imageChoosen = false
    lateinit var uri: Uri
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storageRef: StorageReference
    lateinit var imageUrl: String
    lateinit var databaseReference: DatabaseReference
    lateinit var progressDialog: Dialog
    var userId = ""
    var username = ""
    var profilePic = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addpost_dialog)
        firebaseStorage = FirebaseStorage.getInstance()
        storageRef = firebaseStorage.reference
        databaseReference = Firebase.database.reference
        progressDialog = Dialog(this)

        userId = this!!.getSharedPreferences(
            getString(R.string.sharedPreference),
            Context.MODE_PRIVATE
        ).getString("UID", "").toString()

        databaseReference.child("users").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    username = snapshot.child("username").value.toString()
                    profilePic = snapshot.child("profileImageUrl").value.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        preview.setOnClickListener {
            if (displayText.text.toString().trim() == "" || displayText.text.toString() == null)
                displayText.setError("This cannot be empty")
            else if (!imageChoosen)
                Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show()
            else
                previewPost()
        }

        addImage.setOnClickListener {
            chooseImage()
        }

        post.setOnClickListener {
            postAnImage()
        }

        cancel.setOnClickListener {
            finish()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            addImage.requestLayout()
            addImage.setImageURI(data.data)
            uri = data.data!!
            imageChoosen = true
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Pick an Image"), GALLERY_REQUEST_CODE)
    }

    private fun previewPost() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
            displayText.setTextAppearance(android.R.style.TextAppearance_Material_Display1)
        else
            displayText.setTextAppearance(this, android.R.style.TextAppearance_Material_Display1)

        displayText.setBackgroundColor(Color.parseColor("#B53CB371"))
        displayText.setTextColor(Color.WHITE)
        displayText.layoutParams.width = MATCH_PARENT

        addImage.layoutParams.height = MATCH_PARENT
        addImage.layoutParams.width = MATCH_PARENT
        addImage.scaleType = ImageView.ScaleType.CENTER_CROP

        preview.visibility = View.GONE
        post.visibility = View.VISIBLE
    }

    private fun postAnImage() {
        val imageId = UUID.randomUUID().toString()
        val riversRef: StorageReference = storageRef.child("posts/$imageId")

        riversRef.putFile(uri).addOnProgressListener {
            var progressPercent = (100.0 * it.bytesTransferred / it.totalByteCount)
            showProgressDialog("Precentage: ${progressPercent.toInt()} ")
        }
            .continueWithTask(Continuation<UploadTask.TaskSnapshot?, Task<Uri?>?> { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                riversRef.downloadUrl
            }).addOnCompleteListener(OnCompleteListener<Uri?> { task ->
                if (task.isSuccessful) {
                    imageUrl = task.result.toString()
                    val nfRef = databaseReference.child("newsfeed")
                    var childrenCount: Long? = null
                    nfRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            childrenCount = snapshot.children.last().key?.toLong()
                            Log.d("Andar", "$childrenCount")
                            val pid = (childrenCount!! + 1).toString()
                            databaseReference.child("newsfeed").child(pid).setValue(
                                Data(
                                    moreInfo.text.toString(),
                                    0,
                                    displayText.text.toString().trim(),
                                    imageUrl,
                                    profilePic,
                                    0,
                                    0,
                                    System.currentTimeMillis() / 1000,
                                    userId,
                                    username
                                )
                            )
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    progressDialog.dismiss()
                } else {
                    Toast.makeText(this, "Failed to upload", Toast.LENGTH_SHORT)
                    progressDialog.dismiss()
                }
            })
    }

    private fun showProgressDialog(text: String) {
        progressDialog.setContentView(R.layout.progressdialog)
        progressDialog.percent.text = text

        progressDialog.show()
    }
}

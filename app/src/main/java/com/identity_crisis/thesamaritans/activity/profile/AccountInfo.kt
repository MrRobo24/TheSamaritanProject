package com.identity_crisis.thesamaritans.activity.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.model.User
import kotlinx.android.synthetic.main.accountinfo.*
import kotlinx.android.synthetic.main.addpost_dialog.*
import kotlinx.android.synthetic.main.fragment_profile.*

class AccountInfo : AppCompatActivity() {
    lateinit var databaseReference: DatabaseReference
    private val GALLERY_REQUEST_CODE = 123
    lateinit var uri: Uri
    lateinit var username: String
    lateinit var email: String
    lateinit var userId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.accountinfo)

        userId = this!!.getSharedPreferences(
            getString(R.string.sharedPreference),
            Context.MODE_PRIVATE
        ).getString("UID", "").toString()

        databaseReference = Firebase.database.reference
        databaseReference.child("users").child(userId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                email = snapshot.child("email").value.toString()
                username = snapshot.child("username").value.toString()
                emailAddress.setText(email)
                userName.setText(username)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


        editUser.setOnClickListener {
            userName.inputType = InputType.TYPE_CLASS_TEXT
            emailAddress.inputType = InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS

            save.visibility = Button.VISIBLE
            editUser.visibility = Button.GONE
            changePhoto.visibility = View.VISIBLE
        }

        changePhoto.setOnClickListener {
            chooseImage()
        }

        save.setOnClickListener {
            editDetails()
        }
    }

    private fun editDetails() {
        databaseReference = Firebase.database.reference
        if(userName.text.toString() != username ){
            databaseReference.child("users").child(userId).child("username").setValue(userName.text.toString()).addOnSuccessListener {

                save.visibility = Button.GONE
                editUser.visibility = Button.VISIBLE
                changePhoto.visibility = View.GONE
                userName.inputType = InputType.TYPE_NULL
                emailAddress.inputType = InputType.TYPE_NULL

                Toast.makeText(this,"Updated your details",Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(this,"Details could not be updated",Toast.LENGTH_LONG).show()
                }
        }
        if(emailAddress.text.toString() != email)
        {
            databaseReference.child("users").child(userId).child("email").setValue(emailAddress.text.toString()).addOnSuccessListener {

                save.visibility = Button.GONE
                editUser.visibility = Button.VISIBLE
                changePhoto.visibility = View.GONE
                userName.inputType = InputType.TYPE_NULL
                emailAddress.inputType = InputType.TYPE_NULL

                Toast.makeText(this,"Updated your details",Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(this,"Details could not be updated",Toast.LENGTH_LONG).show()
            }
        }

        else{
            save.visibility = Button.GONE
            editUser.visibility = Button.VISIBLE
            changePhoto.visibility = View.GONE
            userName.inputType = InputType.TYPE_NULL
            emailAddress.inputType = InputType.TYPE_NULL

            Toast.makeText(this,"Nothing to change",Toast.LENGTH_LONG).show()
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Pick an Image"), GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            profileImage.requestLayout()
            profileImage.setImageURI(data.data)
            uri = data.data!!
        }
    }
}
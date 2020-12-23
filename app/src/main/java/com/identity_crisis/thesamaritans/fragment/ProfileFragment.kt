package com.identity_crisis.thesamaritans.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.identity_crisis.thesamaritans.activity.profile.AddPost
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.activity.profile.AccountInfo
import com.identity_crisis.thesamaritans.activity.launcher.LoginActivity
import com.identity_crisis.thesamaritans.activity.profile.ProfileSettings
import com.identity_crisis.thesamaritans.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.accountinfo.*
import kotlinx.android.synthetic.main.addpost_dialog.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.post_row_layout.view.*
import java.net.URI

class ProfileFragment : Fragment() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var addPost: CardView
    lateinit var personalDetails: CardView
    lateinit var accountSettings: CardView
    var GALLERY_REQUEST_CODE = 123
    lateinit var uri: Uri


    val TAG = "Profile Fragement"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        sharedPreferences = context!!.getSharedPreferences(
            getString(R.string.sharedPreference),
            Context.MODE_PRIVATE
        )

        var userId = activity?.getSharedPreferences(
            getString(R.string.sharedPreference),
            Context.MODE_PRIVATE
        )?.getString("UID", "").toString()

        var databaseReference: DatabaseReference = Firebase.database.reference
        databaseReference.child("users").child(userId).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ProfileName.text = snapshot.child("username").value.toString()
                var imageUrl = snapshot.child("profileImageUrl").value.toString()
                if(imageUrl != "")
                    Picasso.get().load(imageUrl).error(R.mipmap.ic_addimage).into(profile_image)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        addPost = view.findViewById(R.id.addpost)
        personalDetails = view.findViewById(R.id.personalDetails)
        accountSettings = view.findViewById((R.id.accountSettings))

        addPost.setOnClickListener {
            val intent = Intent(activity, AddPost::class.java)
            startActivity(intent)
        }

        personalDetails.setOnClickListener {
            val intent = Intent(activity, AccountInfo::class.java)
            startActivity(intent)
        }

        accountSettings.setOnClickListener {
            val intent = Intent(activity, ProfileSettings::class.java)
            startActivity(intent)
        }

        view.profile_image.setOnClickListener{
            chooseImage()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            profile_image.requestLayout()
            profile_image.setImageURI(data.data)
            uri = data.data!!
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Pick an Image"), GALLERY_REQUEST_CODE)
    }
}
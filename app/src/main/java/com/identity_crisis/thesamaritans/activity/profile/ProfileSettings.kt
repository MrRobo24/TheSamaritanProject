package com.identity_crisis.thesamaritans.activity.profile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.activity.launcher.LoginActivity
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileSettings : AppCompatActivity() {

    val TAG = "Profile Fragement"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)



        cvSignOut.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        getSharedPreferences(getString(R.string.sharedPreference), Context.MODE_PRIVATE)
            ?.edit()?.remove("UID")?.apply()
        FirebaseAuth.getInstance().signOut()

        Log.d(TAG, "Sign out successful")
        Log.d(TAG, "Launching Login Activity")

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

}
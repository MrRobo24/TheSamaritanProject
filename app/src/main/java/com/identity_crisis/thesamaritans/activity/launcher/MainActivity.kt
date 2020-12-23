package com.identity_crisis.thesamaritans.activity.launcher

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.identity_crisis.thesamaritans.fragment.NewsfeedFragment
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.activity.WelcomeActivity
import com.identity_crisis.thesamaritans.fragment.ActivityFragment
import com.identity_crisis.thesamaritans.fragment.ConnectFragment
import com.identity_crisis.thesamaritans.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    lateinit var bottomNavigationView: BottomNavigationView
    var previousMenuItem: MenuItem? = null

    override fun onStart() {
        super.onStart()
        verifyCurrentUser()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        //setupToolbar()

        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if (isConnected) {
            Log.d("LoginActivity","Internet available")
            openNewsFeedFragment()
        } else {
            Log.d("LoginActivity","Internet not available")
            alertNoNet()
        }

        setupBottomNav()
    }

    fun alertNoNet() {
        val dialog = AlertDialog.Builder(this@MainActivity)
        dialog.setTitle("Error")
        dialog.setMessage("Internet Connection is Not Found")
        dialog.setPositiveButton("Open Settings") { _, _ ->
            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(settingsIntent)
            ActivityCompat.finishAffinity(this@MainActivity)
        }
        dialog.setNegativeButton("Exit") { _, _ ->
            ActivityCompat.finishAffinity(this@MainActivity)
        }

        dialog.create()
        dialog.show()
    }

    private fun openActivityFragment() {
        val fragment =
            ActivityFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = "Activity Fragment"
        bottomNavigationView.menu.getItem(2).isChecked = true
    }

    private fun verifyCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        Log.d("Main", "Current user:$uid")
        if (uid == null) {
            val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

//    private fun setupToolbar() {
//        supportActionBar?.title = "Toolbar Title"
//        supportActionBar?.setHomeButtonEnabled(true)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//    }


    private fun setupBottomNav() {
        bottomNavigationView.setOnNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {
                R.id.action_news_feed -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            NewsfeedFragment()
                        )
                        .commit()

                    supportActionBar?.title = "News Feed"
                    //Toast.makeText(this, "News Feed", Toast.LENGTH_SHORT).show()
                }
                R.id.action_connect -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            ConnectFragment()
                        )
                        .commit()

                    supportActionBar?.title = "Connect Fragment"
                    //Toast.makeText(this, "Connect", Toast.LENGTH_SHORT).show()
                }
                R.id.action_activity -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            ActivityFragment()
                        )
                        .commit()

                    supportActionBar?.title = "Activity"
                    //Toast.makeText(this, "Activity", Toast.LENGTH_SHORT).show()
                }
                R.id.action_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            ProfileFragment()
                        )
                        .commit()

                    supportActionBar?.title = "Profile"
                    //Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun openNewsFeedFragment() {
        val fragment =
            NewsfeedFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = "News Feed"
        bottomNavigationView.menu.getItem(0).isChecked = true
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)

        when (frag) {
            !is NewsfeedFragment -> openNewsFeedFragment()
            else -> super.onBackPressed()
        }
    }

    //experimenting with toolbar
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.chat_nav_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.menu_new_message -> {
//                Log.d("LatestMessageActivity", "Launching new message activity")
//                val intent = Intent(this@MainActivity, NewMessageActivity::class.java)
//                startActivity(intent)
//            }
//            R.id.menu_sign_out -> {
//                getSharedPreferences( getString(R.string.sharedPreference), Context.MODE_PRIVATE).edit().remove("UID").apply()
//                FirebaseAuth.getInstance().signOut()
//                Log.d("LatestMessageActivity", "Sign out successful")
//                Log.d("LatestMessageActivity", "Launching Login Activity")
//                val intent = Intent(this@MainActivity, LoginActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }


}
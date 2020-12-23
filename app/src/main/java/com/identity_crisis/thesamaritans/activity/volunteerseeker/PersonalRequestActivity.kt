package com.identity_crisis.thesamaritans.activity.volunteerseeker

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.activity.launcher.MainActivity
import com.identity_crisis.thesamaritans.adapter.PersonalRequestViewHolder
import com.identity_crisis.thesamaritans.model.UserList
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class PersonalRequestActivity : AppCompatActivity() {
    lateinit var recyclerPersons: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var btnSend: Button

    var uid=""
    var userCount=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_request)
        recyclerPersons=findViewById(R.id.recyclerPersons)
        btnSend=findViewById(R.id.btnSend)

        btnSend.visibility=View.INVISIBLE

        uid=getSharedPreferences(getString(R.string.sharedPreference), Context.MODE_PRIVATE).getString("UID","").toString()

        supportActionBar?.title="Request Personally"

        btnSend.setOnClickListener {
            if (userCount >= 3) {
                //WILL IMPLEMENT LATER
                val intent = Intent(
                    this@PersonalRequestActivity,
                    MainActivity::class.java
                )
                intent.putExtra("FRAGKEY", "ACTIVITY")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }

        fetchUsers()

    }

    private fun fetchUsers(){

        layoutManager = LinearLayoutManager(this)
        val ref=FirebaseDatabase.getInstance().getReference("/users")
        ref.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                val adapter=GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    Log.d("Main",it.toString())

                    if (it.key.toString()!=uid){
                        val user=it.getValue(UserList::class.java)
                        if(user!=null)
                            adapter.add(PersonalRequestViewHolder(user))
                    }

                }

                adapter.setOnItemClickListener { item, view ->

                    if(userCount<3){
//                        val userItem=item as UserList
                        view.setBackgroundColor(Color.parseColor("#31BF31"))

                        userCount++

                        if(userCount>0){
                            btnSend.visibility=View.VISIBLE
                        }
                    }

                }

                recyclerPersons.adapter=adapter
                recyclerPersons.layoutManager=layoutManager
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}
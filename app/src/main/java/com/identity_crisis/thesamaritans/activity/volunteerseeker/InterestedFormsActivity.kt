package com.identity_crisis.thesamaritans.activity.volunteerseeker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.adapter.InterestedFormsViewPagerAdapter
import com.identity_crisis.thesamaritans.model.Form
import com.identity_crisis.thesamaritans.model.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_interested_forms.*

class InterestedFormsActivity : AppCompatActivity() {

    var setInterestedForms: HashSet<String>? = hashSetOf()
    var uid = ""

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interested_forms)

        uid = getSharedPreferences(
            getString(R.string.sharedPreference),
            Context.MODE_PRIVATE
        ).getString("UID", "").toString()

        setInterestedForms =
            intent.extras!!.getSerializable("setInterestedForms") as HashSet<String>


        fetchInterestedForm()
        viewpagerInterestedForms.adapter = adapter
//        viewpagerInterestedForms.adapter=
//            InterestedFormsViewPagerAdapter(this,listInterestedForms!!)

    }

    private fun fetchInterestedForm() {

        setInterestedForms!!.forEach {
            val refFormOwner =
                FirebaseDatabase.getInstance().getReference("/form-owner/$it")

            refFormOwner.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val ownerId = snapshot.value.toString()
                    lateinit var owner: User


                    val ownerUserRef =
                        FirebaseDatabase.getInstance().getReference("/users/$ownerId")
                    ownerUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            owner = snapshot.getValue(User::class.java)!!


                            val refForm =
                                refFormOwner.parent!!.parent!!.child("/forms/${ownerId}/${it}")

                            refForm.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val form = snapshot.getValue(Form::class.java)

                                    val formId = it



                                    adapter.add(
                                        InterestedFormsViewPagerAdapter(
                                            applicationContext,
                                            formId,
                                            owner,
                                            uid,
                                            form!!
                                        )
                                    )
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })


                        }

                    })


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

    }
}
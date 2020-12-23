package com.identity_crisis.thesamaritans.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.activity.volunteerseeker.SeekHelpFormActivity
import com.identity_crisis.thesamaritans.adapter.InterestedUsersViewHolder
import com.identity_crisis.thesamaritans.adapter.SeekHelpHistoryViewHolder
import com.identity_crisis.thesamaritans.model.Form
import com.identity_crisis.thesamaritans.model.FormList
import com.identity_crisis.thesamaritans.model.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_seek_help.*
import kotlinx.android.synthetic.main.fragment_seek_help.view.*

class SeekHelpFragment : Fragment() {

    lateinit var btnAddForm: FloatingActionButton
    lateinit var recyclerHistory: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var txtHistory: TextView

    val adapter = GroupAdapter<GroupieViewHolder>()
    val TAG = "SeekHelpFragment"
    var uid = ""

    var activeFormId: String? = null
    lateinit var activeForm: Form

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_seek_help, container, false)

        btnAddForm = view.findViewById(R.id.btnAddForm)
        recyclerHistory = view.findViewById(R.id.recyclerHistory)
        txtHistory = view.findViewById(R.id.txtHistory)

        uid = context!!.getSharedPreferences(
            getString(R.string.sharedPreference),
            Context.MODE_PRIVATE
        ).getString("UID", "").toString()


        checkActiveForm(view)

        return view
    }

    private fun checkActiveForm(view: View) {

        uid = FirebaseAuth.getInstance().uid.toString()

        val ref = FirebaseDatabase.getInstance().getReference("/forms/${uid}")


        Log.d(TAG, "Reference: $ref")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                Log.d(TAG, "Form Snapshot: $snapshot")
                if (snapshot.value != null) {
                    val currForm = snapshot.children.last()
                    val currStatus = currForm.child("formStatus").value.toString()

                    Log.d(TAG, "Current Last Form: $currForm")
                    if (currStatus == "1") {
                        Log.d(TAG, "Current Last Form Status: $currStatus")

                        activeFormId = currForm.key.toString()
                        activeForm = currForm.getValue(Form::class.java)!!
                        view.rlFormsContainer.visibility = View.GONE
                        view.rlCurrFormContainer.visibility = View.VISIBLE

                        Log.d(TAG, "CALLING INTERESTED USERS")
                        displayInterestedUsers(view)
                    } else {
                        Log.d(TAG, "Denied Form Status: $currStatus")

                        view.rlCurrFormContainer.visibility = View.GONE
                        view.rlFormsContainer.visibility = View.VISIBLE

                        Log.d(TAG, "CALLING FORM HISTORY")
                        displayHistory(view)
                    }
                } else {
                    Log.d(TAG, "No forms uploaded by this user")
                    view.rlCurrFormContainer.visibility = View.GONE
                    view.rlFormsContainer.visibility = View.VISIBLE
                    displayHistory(view)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "DATABASE ERROR")
            }

        })


    }

    private fun displayInterestedUsers(view: View?) {

        view?.txtFormTitle?.text = activeForm.category.toString()
        view?.txtViewFormDetails?.setOnClickListener {
            TODO("SET ON CLICK LISTENER")
        }

        recyclerInterestedusers.adapter = adapter

        val ref = FirebaseDatabase.getInstance().getReference("/active-forms/${activeFormId}")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if (!snapshot.hasChildren()) {
                    if (txtNoVolunteers != null) {
                        txtNoVolunteers.visibility = View.VISIBLE
                    }

                } else {
                    if (txtNoVolunteers != null) {
                        txtNoVolunteers.visibility = View.GONE
                    }

                }

                snapshot.children.forEach {
                    val interestedUserId = it.key.toString()
                    val interestedUserStatus = it.value.toString()

                    val userRef =
                        FirebaseDatabase.getInstance().getReference("/users/${interestedUserId}")

                    userRef.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val email = snapshot.child("email").value.toString()
                            val username = snapshot.child("username").value.toString()
                            val profileImageUrl = snapshot.child("profileImageUrl").value.toString()

                            Log.d(TAG, email + "\t" + username + "\t" + profileImageUrl)

                            val interestedUser =
                                User(interestedUserId, username, email, profileImageUrl)
                            adapter.add(
                                InterestedUsersViewHolder(
                                    interestedUser,
                                    activeFormId,
                                    context!!,
                                    interestedUserStatus,
                                    this@SeekHelpFragment
                                )
                            )
                        }
                    })

                }
            }

        })
    }

    private fun displayHistory(view: View) {
        btnAddForm = view.findViewById(R.id.btnAddForm)
        recyclerHistory = view.findViewById(R.id.recyclerHistory)
        txtHistory = view.findViewById(R.id.txtHistory)


        fetchHistory()
        recyclerHistory.adapter = adapter
        layoutManager = LinearLayoutManager(activity as Context)
        recyclerHistory.layoutManager = layoutManager

        btnAddForm.setOnClickListener {
            val intent = Intent(context, SeekHelpFormActivity::class.java)
            startActivity(intent)
        }

    }

    private fun fetchHistory() {
        val formRef = FirebaseDatabase.getInstance().getReference("/forms/$uid")
        formRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                adapter.clear()
                Log.d(TAG, "Scanning all the forms under $uid")

                if (snapshot.hasChildren())
                    txtHistory.text = "Requests History"

                snapshot.children.forEach {
                    val formKey = it.key
                    Log.d(TAG, "Form id $formKey")

                    val formFetched = it.getValue(FormList::class.java)
                    if (formFetched != null) {
                        adapter.add(SeekHelpHistoryViewHolder(formKey!!, formFetched))
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}
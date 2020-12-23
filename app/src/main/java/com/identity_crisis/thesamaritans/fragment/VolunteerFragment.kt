package com.identity_crisis.thesamaritans.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.activity.volunteerseeker.InterestedFormsActivity
import com.identity_crisis.thesamaritans.adapter.VolunteerRequestViewHolder
import com.identity_crisis.thesamaritans.model.Form
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_activity.*
import kotlinx.android.synthetic.main.fragment_volunteer.*
import kotlinx.android.synthetic.main.fragment_volunteer.view.*
import kotlinx.android.synthetic.main.single_volunteer_item.*


class VolunteerFragment : Fragment() {

    lateinit var recyclerRequests: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    val adapter = GroupAdapter<GroupieViewHolder>()
    val TAG = "VolunteerFragment"
    var setInterestedForms: HashSet<String>? = hashSetOf()


    var uid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_volunteer, container, false)

        recyclerRequests = view.findViewById(R.id.recyclerRequests)

        uid = context!!.getSharedPreferences(
            getString(R.string.sharedPreference),
            Context.MODE_PRIVATE
        ).getString("UID", "").toString()

        checkForInterestedForms(view)

//        fetchRequests()
//        recyclerRequests.adapter = adapter
//        layoutManager = LinearLayoutManager(activity as Context)
//        recyclerRequests.layoutManager = layoutManager

        view.btnInterestedForms.setOnClickListener {
            val intent = Intent(
                context,
                InterestedFormsActivity::class.java
            )
            intent.putExtra("setInterestedForms", setInterestedForms)
            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        return view
    }

    private fun checkForInterestedForms(view: View) {
        val ref = FirebaseDatabase.getInstance().getReference("/interested-users/${uid}")

        if (ref != null) {

            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()) {
                        view.btnInterestedForms.visibility = View.VISIBLE
                    }

                    snapshot.children.forEach {
                        setInterestedForms!!.add(it.key.toString())
                    }

                    fetchRequests()
                    recyclerRequests.adapter = adapter
                    //layoutManager = LinearLayoutManager(activity as Context)
                    //recyclerRequests.layoutManager = layoutManager

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        } else {
            fetchRequests()
            recyclerRequests.adapter = adapter
            //layoutManager = LinearLayoutManager(activity as Context)
            //recyclerRequests.layoutManager = layoutManager
        }
    }

    private fun fetchRequests() {

        val formRef = FirebaseDatabase.getInstance().getReference("/forms")
        formRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                adapter.clear()

                snapshot.children.forEach {
                    Log.d(TAG, "Scanning all the forms ${snapshot}")
                    if (it.key.toString() != uid) {
                        val userKey = it.key
                        it.children.forEach {


                            if (!setInterestedForms!!.contains(it.key.toString())) {
                                val formFetched = it.getValue(Form::class.java)
                                val formKey = it.key.toString()
                                if (formFetched != null) {
                                    adapter.add(
                                        VolunteerRequestViewHolder(
                                            formKey,
                                            userKey!!,
                                            formFetched,
                                            this@VolunteerFragment
                                        )
                                    )

                                    if (progressBarVol != null) {
                                        Log.d("BAR", "Making Progress Bar Invisible")
                                        progressBarVol.visibility = View.INVISIBLE
                                    }
                                    if (containerVolunteer != null) {
                                        Log.d("BAR", "Making Volunteer Fragment Visible")
                                        containerVolunteer.visibility = View.VISIBLE
                                    }
                                }

                            }
                        }
                    }
                }

                adapter.setOnItemClickListener { item, view ->

                    Log.d("Main Items user id", (item as VolunteerRequestViewHolder).userId)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}
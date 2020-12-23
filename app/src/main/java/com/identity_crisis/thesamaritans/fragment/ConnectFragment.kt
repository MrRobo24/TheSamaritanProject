package com.identity_crisis.thesamaritans.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import com.identity_crisis.thesamaritans.activity.connect.NewMessageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.activity.launcher.RegisterActivity
import com.identity_crisis.thesamaritans.activity.connect.ChatLogActivity
import com.identity_crisis.thesamaritans.adapter.LatestMessageItemAdapter
import com.identity_crisis.thesamaritans.model.ChatMessage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_connect.*
import kotlinx.android.synthetic.main.fragment_connect.view.*

class ConnectFragment : Fragment() {

    val adapter = GroupAdapter<GroupieViewHolder>()
    val latestMessagesMap = HashMap<String, ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_connect, container, false)


        view.rvLatestMessages.adapter = adapter
        view.rvLatestMessages.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(context, ChatLogActivity::class.java)
            //intent.putExtra(USER_KEY ,userItem.username)
            intent.putExtra(NewMessageActivity.USER_KEY, (item as LatestMessageItemAdapter).user)
            startActivity(intent)
        }

        //verifyUserLoggedIn()

        listenForLatestMessages()
        return view
    }


    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/${fromId}")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.hasChildren()) {
                    Log.d("BAR", "NO CHILDREN, CHANGING VISIBILITY")
                    txtNoMessages.visibility = View.VISIBLE
                    refreshRV()
                } else {
                    txtNoMessages.visibility = View.GONE
                }
            }

        })

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRV()
                //adapter.add(LatestMessageItemAdapter(chatMessage))
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRV()
            }


            override fun onCancelled(error: DatabaseError) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

        })
    }

    private fun refreshRV() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageItemAdapter(it))
        }

        if (progressBarCNCT != null) {
            Log.d("BAR", "Making Progress Bar Invisible")
            progressBarCNCT.visibility = View.INVISIBLE
        }
        if (rvLatestMessages != null) {
            Log.d("BAR", "Making Connect Fragment Visible")
            rvLatestMessages.visibility = View.VISIBLE
        }

    }

}
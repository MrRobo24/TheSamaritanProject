package com.identity_crisis.thesamaritans.activity.connect

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.adapter.ChatFromItemAdapter
import com.identity_crisis.thesamaritans.adapter.ChatToItemAdapter
import com.identity_crisis.thesamaritans.model.ChatMessage
import com.identity_crisis.thesamaritans.model.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        //supportActionBar?.title = "Chat Log"


        rvChatLog.adapter = adapter

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY) as User
        txtChatUserName.text = user.username
        supportActionBar?.title = user.username

        //setUpDummyData()
        listenForMessages()

        btnSendMessage.setOnClickListener {
            Log.d(TAG, "Attempting to send message")
            val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

            if (isConnected) {
                Log.d(TAG,"Internet available")
                performSendMessage()
            } else {
                Log.d(TAG,"Internet not available")
                alertNoNet()
            }
        }
    }

    fun alertNoNet() {
        val dialog = AlertDialog.Builder(this@ChatLogActivity)
        dialog.setTitle("Error")
        dialog.setMessage("Internet Connection is Not Found")
        dialog.setPositiveButton("Open Settings") { _, _ ->
            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(settingsIntent)
            ActivityCompat.finishAffinity(this@ChatLogActivity)
        }
        dialog.setNegativeButton("Exit") { _, _ ->
            ActivityCompat.finishAffinity(this@ChatLogActivity)
        }

        dialog.create()
        dialog.show()
    }

    private fun listenForMessages() {
        //val fromId = FirebaseAuth.getInstance().uid as String
        val fromId = getSharedPreferences(
            getString(R.string.sharedPreference),
            Context.MODE_PRIVATE
        )?.getString("UID", "").toString()
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY) as User
        val toId = user.uid

        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                Log.d(TAG, chatMessage?.text.toString())

                if (chatMessage?.fromId == fromId)
                    adapter.add(ChatToItemAdapter(chatMessage?.text.toString()))
                else
                    adapter.add(ChatFromItemAdapter(chatMessage?.text.toString()))

                rvChatLog.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

        })
    }

    private fun performSendMessage() {
        val text = txtNewMessage.text.toString().trim()
        //val fromId = FirebaseAuth.getInstance().uid as String
        val fromId = getSharedPreferences(
            getString(R.string.sharedPreference),
            Context.MODE_PRIVATE
        )?.getString("UID", "").toString()
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY) as User
        val toId = user.uid

        val reference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()


        val chatMessage = ChatMessage(
            reference.key as String,
            text,
            fromId,
            toId,
            System.currentTimeMillis() / 1000
        )
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved Chat Message on Firebase ${reference.key}")
                txtNewMessage.text.clear()
                rvChatLog.scrollToPosition(adapter.itemCount - 1)
            }

        toReference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved Chat for receiver on Firebase ${toReference.key}")
            }

        val latestMessageRef = FirebaseDatabase
            .getInstance()
            .getReference("/latest-messages/${fromId}/${toId}")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase
            .getInstance()
            .getReference("/latest-messages/${toId}/${fromId}")
        latestMessageToRef.setValue(chatMessage)
    }


    private fun setUpDummyData() {
        val adapter = GroupAdapter<GroupieViewHolder>()
        adapter.add(ChatFromItemAdapter("From message"))
        adapter.add(ChatToItemAdapter("To Message"))

        rvChatLog.adapter = adapter
    }
}
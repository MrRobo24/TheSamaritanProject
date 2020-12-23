package com.identity_crisis.thesamaritans.adapter

import android.content.Intent
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.model.ChatMessage
import com.identity_crisis.thesamaritans.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_messages_row.view.*

class LatestMessageItemAdapter(val chatMessage: ChatMessage) : Item<GroupieViewHolder>() {
    var user: User? = null

    override fun getLayout(): Int {
        return R.layout.latest_messages_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        var chatPartnerId: String = ""
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        viewHolder.itemView.txtMessagePeek.text = chatMessage.text
        val ref = FirebaseDatabase.getInstance().getReference("/users/${chatPartnerId}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)
                viewHolder.itemView.txtUserName.text = user?.username


                if (user?.profileImageUrl != "") {
                    Picasso.get().load(user?.profileImageUrl)
                        .into(viewHolder.itemView.civImagePreview)
                    viewHolder.itemView.imgImagePreview.visibility = View.GONE
                    Log.d("UserItemAdapter", "IMAGE LOADED")
                }
                Log.d("UserItemAdapter", "IMAGE UNAVAILABLE")
            }

        })

    }
}
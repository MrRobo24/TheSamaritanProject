package com.identity_crisis.thesamaritans.adapter

import android.util.Log
import android.view.View
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class UserItemAdapter(val user: User) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txtUserName.text = user.username
        if (user.profileImageUrl != "") {
            Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.civImagePreview)
            viewHolder.itemView.imgImagePreview.visibility = View.GONE
            Log.d("UserItemAdapter", "IMAGE LOADED")
        }
        Log.d("UserItemAdapter", "IMAGE UNAVAILABLE")
    }
}
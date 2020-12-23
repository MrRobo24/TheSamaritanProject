package com.identity_crisis.thesamaritans.adapter

import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.model.UserList
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.single_request_row.view.*

class PersonalRequestViewHolder(val user: UserList): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txtUserId.text=user.username
        viewHolder.itemView.txtUserEmail.text=user.email

    }

    override fun getLayout(): Int {
        return R.layout.single_request_row
    }
}
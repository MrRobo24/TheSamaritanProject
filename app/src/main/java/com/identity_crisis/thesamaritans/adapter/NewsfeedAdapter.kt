package com.identity_crisis.thesamaritans.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.model.Data
import com.identity_crisis.thesamaritans.model.UserList
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.post_row_layout.view.*
import kotlinx.android.synthetic.main.post_row_layout.view.imageView3

class NewsfeedAdapter(val d : Data, val u : UserList) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//        viewHolder.itemView.name.text = d.name
        viewHolder.itemView.info3.text = d.name
        viewHolder.itemView.profilename.text = d.username

//        Picasso.get().load(d.Photo).into(imageView)
       Picasso.get().load(d.photo).error(R.drawable.bgimage).into(viewHolder.itemView.imageView3)
        //Picasso.get().load(d.profileImageUrl).error(R.drawable.ic_profile).into(viewHolder.itemView.profile_image)
    }

    override fun getLayout(): Int {
        return R.layout.post_row_layout
    }

}
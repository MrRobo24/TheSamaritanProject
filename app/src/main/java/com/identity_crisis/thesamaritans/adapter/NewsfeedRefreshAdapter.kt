package com.identity_crisis.thesamaritans.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.model.Data
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.post_row_layout.view.*

class NewsfeedRefreshAdapter() : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    }
    override fun getLayout(): Int {
        return R.layout.row_newsfeed_refresh
    }

}
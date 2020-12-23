package com.identity_crisis.thesamaritans.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.model.Data
import com.squareup.picasso.Picasso

class NewsfeedRefreshHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.row_newsfeed_refresh, parent, false)) {
    fun bind(d: Data) {
        Thread.sleep(1500)
    }

}

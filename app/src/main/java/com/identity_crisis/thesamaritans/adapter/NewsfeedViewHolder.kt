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

class NewsfeedViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.post_row_layout, parent, false)) {
//    val name: TextView = itemView.findViewById(R.id.name)
    val imageView: ImageView = itemView.findViewById(R.id.imageView3)
//    val info1: TextView = itemView.findViewById(R.id.info1)
//    val info2: TextView = itemView.findViewById(R.id.info2)
    val info3: TextView = itemView.findViewById(R.id.info3)

    fun bind(d: Data) {
//        name.text = d.name
//        info1.text = d.Info1
//        info2.text = d.Info2
//        info3.text = d.Info3
        Log.d("CHECK", d.photo.toString())
//        Picasso.get().load(d.Photo).into(imageView)
        Picasso.get()
            .load(d.photo)
            .error(R.drawable.ic_launcher_background)
            .into(imageView)
//        Glide.with(imageView.context).load(d.Photo).error(R.drawable.ic_like).into(imageView)
    }

}

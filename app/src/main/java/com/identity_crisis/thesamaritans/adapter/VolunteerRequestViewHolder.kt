package com.identity_crisis.thesamaritans.adapter

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.fragment.VolunteerFragment
import com.identity_crisis.thesamaritans.model.Form
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.single_volunteer_item.view.*

class VolunteerRequestViewHolder(
    val formKey: String,
    val userKey: String,
    val form: Form,
    val obj: VolunteerFragment
) : Item<GroupieViewHolder>() {


    var userId = ""

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        userId = userKey
        viewHolder.itemView.txtTitleAns.text = form.title
        viewHolder.itemView.txtStateAns.text = form.state
        viewHolder.itemView.txtTypeAns.text = form.type
//        viewHolder.itemView.txtCityPincodeAns.text = "${form.city}, ${form.pincode}"
//        viewHolder.itemView.txtDescriptionAns.text = form.description
//        viewHolder.itemView.txtContactAns.text = form.contact

        viewHolder.itemView.btnVolunteer.setOnClickListener {
            val refInterestedUsers =
                FirebaseDatabase.getInstance().getReference("/interested-users/${obj.uid}")
            refInterestedUsers.child(formKey).setValue("-1")

//            obj.listInterestedForms!![formKey.toString()] = form
            //obj.listInterestedForms!!.add(form)
            obj.setInterestedForms!!.add(formKey)

            val refActiveForms =
                FirebaseDatabase.getInstance().getReference("/active-forms/${formKey}")
            refActiveForms.child(obj.uid).setValue("-1")

            obj.adapter.removeGroupAtAdapterPosition(position)
        }
    }

    override fun getLayout(): Int {
        return R.layout.single_volunteer_item
    }
}
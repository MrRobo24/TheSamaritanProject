package com.identity_crisis.thesamaritans.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.activity.connect.ChatLogActivity
import com.identity_crisis.thesamaritans.activity.connect.NewMessageActivity
import com.identity_crisis.thesamaritans.model.Form
import com.identity_crisis.thesamaritans.model.User
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.single_interested_form.view.*

class InterestedFormsViewPagerAdapter(
    val context: Context,
    val formId: String,
    val owner: User,
    val uid: String,
    val form: Form
) :
    Item<GroupieViewHolder>() {

    val TAG = "InterestedFormsActivity"


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.txtCategoryAns.text = form.category
        viewHolder.itemView.txtTypeAns.text = form.type
        viewHolder.itemView.txtStateAns.text = form.state
        viewHolder.itemView.txtDescriptionAns.text = form.description
        viewHolder.itemView.txtCityPincodeAns.text = form.pincode
        viewHolder.itemView.txtContactAns.text = form.contact

        checkStatus(viewHolder)

        viewHolder.itemView.btnChat.setOnClickListener {
            Log.d(TAG, "Launching Chat Log Activity with: ${owner}")
            val intent = Intent(context, ChatLogActivity::class.java)
            //intent.putExtra(USER_KEY ,userItem.username)
            intent.putExtra(NewMessageActivity.USER_KEY, owner)
//            intent.putExtra("interestedUserStatus", statusFlag)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)

        }

        viewHolder.itemView.btnActivate.setOnClickListener {
            Log.d(TAG, "Trying to activate")
            val refForStatus =
                FirebaseDatabase.getInstance().getReference("/active-forms/$formId/$uid")
            val refForInterestedUsers = refForStatus
                .parent
                ?.parent
                ?.parent
                ?.child("/interested-users/$uid/$formId")
            refForStatus.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {


                    var status = snapshot.value.toString()
                    Log.d(TAG, "Changing transaction status from $status")
                    status = (status.toInt() + 1).toString()

                    refForStatus.setValue(status)
                    refForInterestedUsers?.setValue(status)
                    viewHolder.itemView.btnActivate.visibility = View.GONE
                    Log.d(TAG, "Changing transaction status to $status")

                    if (status == "3") {
                        Log.d(TAG, "TRANSACTION ACTIVE")
                        Toast.makeText(context, "ACTIVATION SUCCESSFUL", Toast.LENGTH_LONG)
                            .show()
                    }


//                    when (status) {
//                        "0" -> {
//                            status = "1"
//                            refForStatus.setValue(status)
//                            refForInterestedUsers?.setValue(status)
//                            viewHolder.itemView.btnActivate.visibility = View.GONE
//                            Log.d(TAG, "Changing transaction status to 1")
//                        }
//
//                        "1" -> {
//                            status = "2"
//                            refForStatus.setValue(status)
//                            refForInterestedUsers?.setValue(status)
//                            Log.d(TAG, "Changing transaction status to 2")
//                            Log.d(TAG, "TRANSACTION ACTIVE")
//                            viewHolder.itemView.btnActivate.visibility = View.GONE
//                            Toast.makeText(context, "ACTIVATION SUCCESSFUL", Toast.LENGTH_LONG)
//                                .show()
//                        }
//                    }
                }

            })

        }

    }

    override fun getLayout(): Int {
        return R.layout.single_interested_form
    }

    private fun checkStatus(viewHolder: GroupieViewHolder) {
        val refActiveForms =
            FirebaseDatabase.getInstance().getReference("/active-forms/${formId}/${uid}")
        refActiveForms.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.value

                if (status != "-1") {
                    viewHolder.itemView.llButtonContainer.visibility = View.VISIBLE
                }

                when (status) {
                    "-1" -> {
                        viewHolder.itemView.llButtonContainer.visibility = View.GONE
                    }
                    "1", "3" -> {
                        viewHolder.itemView.btnActivate.visibility = View.GONE
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}

package com.identity_crisis.thesamaritans.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.activity.connect.ChatLogActivity
import com.identity_crisis.thesamaritans.activity.connect.NewMessageActivity
import com.identity_crisis.thesamaritans.fragment.SeekHelpFragment
import com.identity_crisis.thesamaritans.model.User
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.interested_user_row.view.*
import kotlinx.android.synthetic.main.single_request_row.view.txtUserEmail
import kotlinx.android.synthetic.main.single_request_row.view.txtUserId

class InterestedUsersViewHolder(
    val user: User,
    val activeFormId: String?,
    val context: Context,
    val interestedUserStatus: String,
    val obj: SeekHelpFragment
) :
    Item<GroupieViewHolder>() {

    val TAG = "InterestedUserVH"
    var statusFlag = "-1"

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txtUserId.text = user.username
        viewHolder.itemView.txtUserEmail.text = user.email
        val activateButton: Button = viewHolder.itemView.btnActivate
        val initChatButton: Button = viewHolder.itemView.btnInitiateInterestesUser
        val denyButton: Button = viewHolder.itemView.btnDenyInterestesUser
        val activateContainer = viewHolder.itemView.llActivateContainer
        val dropDown = viewHolder.itemView.btnDropDown


        val status = interestedUserStatus
        statusFlag = status
        when (status) {
            "-1" -> {
                activateContainer.visibility = View.GONE
            }
            "0" -> {
                activateContainer.visibility = View.VISIBLE
            }
            "2", "3" -> {
                activateContainer.visibility = View.GONE
            }
        }

        if (status != "-1") {
            initChatButton.text = "Chat"
        }




        dropDown.setOnClickListener {
            if (activateButton.visibility == View.VISIBLE) {
                dropDown.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_arrow_drop_down_24))
                activateButton.visibility = View.GONE
            } else {
                dropDown.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_arrow_drop_up_24))
                activateButton.visibility = View.VISIBLE
            }

        }

        activateButton.setOnClickListener {
            Log.d(TAG, "Trying to activate: ${user.uid}")
            Log.d(TAG, "Trying to activate")
            val refForStatus =
                FirebaseDatabase.getInstance()
                    .getReference("/active-forms/$activeFormId/${user.uid}")
            val refForInterestedUsers = refForStatus
                .parent
                ?.parent
                ?.parent
                ?.child("/interested-users/${user.uid}/$activeFormId")
            refForStatus.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    var status = snapshot.value.toString()
                    Log.d(TAG, "Changing transaction status from $status")
                    status = (status.toInt() + 2).toString()
                    Log.d(TAG, "Changing transaction status to $status")
                    refForStatus.setValue(status)
                    refForInterestedUsers?.setValue(status)
                    activateContainer.visibility = View.GONE

                    if (status == "3") {
                        Log.d(TAG, "TRANSACTION ACTIVE")
                        Toast.makeText(context, "ACTIVATION SUCCESSFUL", Toast.LENGTH_LONG)
                            .show()
                    }

                }

            })

        }


        val refActiveForms = FirebaseDatabase
            .getInstance()
            .getReference("/active-forms/$activeFormId/${user.uid}")

        val refInterestedUsers = FirebaseDatabase
            .getInstance()
            .getReference("/interested-users/${user.uid}/${activeFormId}")
//
//
//        refInterestedUser.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val status = snapshot.value.toString()
//                statusFlag = status
//                when (status) {
//                    "-1" -> {
//                    }
//                    "0" -> {
//                        initChatButton.text = "Chat"
//                    }
//                }
//            }
//
////        })


        initChatButton.setOnClickListener {

            when (statusFlag) {
                "-1" -> {
                    //chat is to be initiated
                    statusFlag = "0"
                    refActiveForms.setValue("0")
                    refInterestedUsers.setValue("0")
                    it.btnInitiateInterestesUser.text = "Chat"
                }
                else -> {
                    //chat has already been initiated: LAUNCH CHAT ACTIVITY
                    Log.d(TAG, "Launching Chat Log Activity with: ${user.uid}")
                    val intent = Intent(context, ChatLogActivity::class.java)
                    //intent.putExtra(USER_KEY ,userItem.username)
                    intent.putExtra(NewMessageActivity.USER_KEY, user)
                    intent.putExtra("interestedUserStatus", statusFlag)
                    context.startActivity(intent)
                }

            }

            activateContainer.visibility = View.VISIBLE
        }



        denyButton.setOnClickListener {
            Log.d(TAG, "Removing ${user.uid} from List of Interested Users")
            refActiveForms.removeValue()
            refInterestedUsers.removeValue()
            obj.adapter.removeGroupAtAdapterPosition(position)
        }

    }


    override fun getLayout(): Int {
        return R.layout.interested_user_row
    }
}


package com.identity_crisis.thesamaritans.adapter

import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.model.FormList
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.single_row_history.view.*

class SeekHelpHistoryViewHolder(val formKey:String,val form: FormList): Item<GroupieViewHolder>() {

    var formId=""

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        formId=formKey
        viewHolder.itemView.txtCategory.text=form.category
        viewHolder.itemView.txtState.text=form.state
        viewHolder.itemView.txtType.text=form.type
    }

    override fun getLayout(): Int {
        return R.layout.single_row_history
    }
}
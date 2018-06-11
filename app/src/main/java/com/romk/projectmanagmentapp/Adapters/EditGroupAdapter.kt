package com.romk.projectmanagmentapp.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.romk.projectmanagmentapp.Models.UserModel
import com.romk.projectmanagmentapp.R
import kotlinx.android.synthetic.main.view_edit_group.view.*

class EditGroupAdapter(context: Context, private val users: List<UserModel>, private val leaderPos: Int) : RecyclerView.Adapter<EditGroupAdapter.ViewHolder>() {
    private var lastCheckedPos = -1
    private var lastChecked = RadioButton(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditGroupAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).
            inflate(R.layout.view_edit_group, parent, false) as View

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(users[position], position)
    }

    fun getSelectedItem(): Int {
        return lastCheckedPos
    }

    override fun getItemCount() = users.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(userToBind: UserModel, position: Int) {
            if (position == leaderPos && lastCheckedPos == -1) {
                itemView.member_radio_button.isChecked = true
                lastCheckedPos = position
                lastChecked = itemView as RadioButton
            }
            itemView.member_radio_button.text = userToBind.email
            itemView.member_radio_button.setOnClickListener {
                lastCheckedPos = adapterPosition
                lastChecked.isChecked = false
                lastChecked = itemView as RadioButton
                lastChecked.isChecked = true
                notifyDataSetChanged()
            }
        }
    }
}
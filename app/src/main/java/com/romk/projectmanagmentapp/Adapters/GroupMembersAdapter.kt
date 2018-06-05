package com.romk.projectmanagmentapp.Adapters

import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.romk.projectmanagmentapp.Models.ExtendedGroupModel
import com.romk.projectmanagmentapp.R

class GroupMembersAdapter(private val data: ExtendedGroupModel) : RecyclerView.Adapter<GroupMembersAdapter.ViewHolder>() {

    class ViewHolder(var textView: View) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupMembersAdapter.ViewHolder {
        val textView = LayoutInflater.from(parent.context).
            inflate(R.layout.view_group_member, parent, false) as View

        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val textView = holder.textView.findViewById<TextView>(R.id.view_group_member)
        textView.text = data.members[position].email
        if(data.members[position].email == data.leader.email)
        {
            textView.setTypeface(textView.typeface, Typeface.BOLD)
        }
    }

    override fun getItemCount() = data.members.size
}
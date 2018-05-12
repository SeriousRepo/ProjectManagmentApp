package com.romk.projectmanagmentapp.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.romk.projectmanagmentapp.GroupModel
import com.romk.projectmanagmentapp.R

class GroupsAdapter(private val data: List<GroupModel>) : RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {

    class ViewHolder(var textView: View) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsAdapter.ViewHolder {
        val textView = LayoutInflater.from(parent.context).
                inflate(R.layout.groups_text_view, parent, false) as View

        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.findViewById<TextView>(R.id.group_text_view).text = data[position].groupName
    }

    override fun getItemCount() = data.size
}
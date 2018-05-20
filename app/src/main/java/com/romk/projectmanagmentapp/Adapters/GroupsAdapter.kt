package com.romk.projectmanagmentapp.Adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.romk.projectmanagmentapp.Activities.SingleGroupActivity
import com.romk.projectmanagmentapp.Models.SimpleGroupModel
import com.romk.projectmanagmentapp.R

class GroupsAdapter(activityContext: Context, private val data: List<SimpleGroupModel>) : RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {
    var context = activityContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsAdapter.ViewHolder {
        val textView = LayoutInflater.from(parent.context).
                inflate(R.layout.text_view_groups, parent, false) as View

        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    override fun getItemCount() = data.size

    inner class ViewHolder(textView: View) : RecyclerView.ViewHolder(textView) {

        private var view = textView
        private lateinit var group : SimpleGroupModel

        init {
            view.setOnClickListener{
                val singleGroupActivityIntent = Intent(context, SingleGroupActivity::class.java)
                singleGroupActivityIntent.putExtra("id", group.id)
                context.startActivity(singleGroupActivityIntent)
            }
        }

        fun bindData(groupToBind : SimpleGroupModel) {
            group = groupToBind
            view.findViewById<TextView>(R.id.group_text_view).text = group.groupName
        }
    }
}
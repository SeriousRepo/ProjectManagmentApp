package com.romk.projectmanagmentapp.Adapters

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.support.design.widget.TabLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.romk.projectmanagmentapp.Activities.GroupMembersActivity
import com.romk.projectmanagmentapp.Activities.ListsActivity
import com.romk.projectmanagmentapp.Models.TableModel
import com.romk.projectmanagmentapp.R

class TablesAdapter(activityContext: Context, table: List<TableModel>, private val groupId: Int) : RecyclerView.Adapter<TablesAdapter.ViewHolder>() {
    private var context = activityContext
    private var data = table

    init {
        if (groupId != 0) {
            data = listOf(TableModel(0,"",false,0)) + table
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TablesAdapter.ViewHolder {
        val textView = LayoutInflater.from(parent.context).
            inflate(R.layout.view_tables, parent, false) as View

        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(groupId != 0) {
            if (position == 0) {
                holder.showUsersField(groupId)
            }
            else {
                holder.bindData(data[position])
            }
        }
        else {
            holder.bindData(data[position])
        }
    }

    override fun getItemCount() = data.size

    inner class ViewHolder(textView: View) : RecyclerView.ViewHolder(textView) {

        private var view = textView
        private lateinit var table : TableModel

        fun showUsersField(groupId: Int) {
            view.findViewById<TextView>(R.id.view_table).text = "Group members"
            view.setOnClickListener{
                val groupMembersActivityIntent = Intent(context, GroupMembersActivity::class.java)
                groupMembersActivityIntent.putExtra("groupId", groupId)
                context.startActivity(groupMembersActivityIntent)
            }
        }

        fun bindData(tableToBind : TableModel) {
            table = tableToBind
            view.findViewById<TextView>(R.id.view_table).text = table.name
            view.setOnClickListener{
                val listsActivityIntent = Intent(context, ListsActivity::class.java)
                listsActivityIntent.putExtra("groupId", groupId)
                listsActivityIntent.putExtra("tableId", table.id)
                context.startActivity(listsActivityIntent)
            }
        }
    }
}
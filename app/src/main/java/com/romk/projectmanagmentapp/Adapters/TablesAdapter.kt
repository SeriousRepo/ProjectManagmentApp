package com.romk.projectmanagmentapp.Adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.romk.projectmanagmentapp.Activities.TablesActivity
import com.romk.projectmanagmentapp.Models.TableModel
import com.romk.projectmanagmentapp.R

class TablesAdapter(activityContext: Context, private val data: List<TableModel>) : RecyclerView.Adapter<TablesAdapter.ViewHolder>() {
    var context = activityContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TablesAdapter.ViewHolder {
        val textView = LayoutInflater.from(parent.context).
            inflate(R.layout.view_tables, parent, false) as View

        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    override fun getItemCount() = data.size

    inner class ViewHolder(var textView: View) : RecyclerView.ViewHolder(textView) {

        private var view = textView
        private lateinit var table : TableModel

        init {
            view.setOnClickListener{
                //val singleGroupActivityIntent = Intent(context, TablesActivity::class.java)
                //singleGroupActivityIntent.putExtra("id", table.id)
                //context.startActivity(singleGroupAc`tivityIntent)
            }
        }

        fun bindData(tableToBind : TableModel) {
            table = tableToBind
            view.findViewById<TextView>(R.id.view_table).text = table.name
        }
    }
}
package com.romk.projectmanagmentapp.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.romk.projectmanagmentapp.Models.ListModel
import com.romk.projectmanagmentapp.R

class ListsAdapter(private val data: List<ListModel>) : RecyclerView.Adapter<ListsAdapter.ViewHolder>() {

    class ViewHolder(var textView: View) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListsAdapter.ViewHolder {
        val textView = LayoutInflater.from(parent.context).
            inflate(R.layout.view_list, parent, false) as View

        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val textView = holder.textView.findViewById<TextView>(R.id.list_name)
        textView.text = data[position].name
    }

    override fun getItemCount() = data.size
}
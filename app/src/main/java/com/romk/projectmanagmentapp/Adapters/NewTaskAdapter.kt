package com.romk.projectmanagmentapp.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.romk.projectmanagmentapp.Models.UserModel
import com.romk.projectmanagmentapp.R
import kotlinx.android.synthetic.main.view_new_task.view.*

class NewTaskAdapter(private val users: List<UserModel>) : RecyclerView.Adapter<NewTaskAdapter.ViewHolder>() {
    private var lastChecked = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewTaskAdapter.ViewHolder {
        val textView = LayoutInflater.from(parent.context).
            inflate(R.layout.view_new_task, parent, false) as View

        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(users[position], position)
    }

    fun getSelectedItem(): Int {
        return lastChecked
    }

    fun unsetLast() {
        (getItemViewType(lastChecked) as RadioButton).isChecked = false
    }

    override fun getItemCount() = users.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(userToBind: UserModel, position: Int) {
            itemView.member_radio_button.text = userToBind.email
            itemView.member_radio_button.setOnClickListener {
                lastChecked = adapterPosition
                unsetLast()
                notifyDataSetChanged()
            }
        }
    }
}
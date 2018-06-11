package com.romk.projectmanagmentapp.Adapters

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.romk.projectmanagmentapp.Models.UserModel
import com.romk.projectmanagmentapp.R
import kotlinx.android.synthetic.main.view_delete_member.view.*
import kotlinx.android.synthetic.main.view_edit_group.view.*

class DeleteGroupMembersAdapter(private val leaderPos: Int, private val memberEmails: ArrayList<String>) : RecyclerView.Adapter<DeleteGroupMembersAdapter.ViewHolder>() {
    private var checked = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeleteGroupMembersAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).
            inflate(R.layout.view_delete_member, parent, false) as View

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(leaderPos, memberEmails[position], position)
    }

    fun getPositionsOfselectedItems(): List<Int> {
        return checked
    }

    override fun getItemCount() = memberEmails.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(leaderPos: Int, email: String, position: Int) {
            if (leaderPos == position) {
                itemView.delete_member_check_box.setTypeface(itemView.delete_member_check_box.typeface, Typeface.BOLD)
            }
            itemView.delete_member_check_box.text = email
            itemView.delete_member_check_box.setOnClickListener {
                if (itemView.delete_member_check_box.isChecked) {
                    checked.add(position)

                }
                else {
                    if (position in checked) {
                        checked.remove(position)
                    }
                }
                notifyDataSetChanged()
            }
        }
    }
}
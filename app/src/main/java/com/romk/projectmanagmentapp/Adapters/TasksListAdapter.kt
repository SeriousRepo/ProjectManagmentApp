package com.romk.projectmanagmentapp.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.romk.projectmanagmentapp.Activities.NewTaskActivity
import com.romk.projectmanagmentapp.Models.TasksModel
import com.romk.projectmanagmentapp.R

class TasksListAdapter(private val context: Context,
                       private val tasksList: List<TasksModel>,
                       private val groupId: Int,
                       private val tableId: Int,
                       private val listId: Int,
                       private val cardId: Int) : RecyclerView.Adapter<TasksListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksListAdapter.ViewHolder {
        val textView = LayoutInflater.from(parent.context).
            inflate(R.layout.view_tasks_list, parent, false) as View

        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tasksListNameTextView.text = tasksList[position].name
        val numberOfChildTextView = holder.tasksLinearLayout.childCount
        val numberOfChild = tasksList[position].tasks.size
        if (numberOfChild < numberOfChildTextView) {
            for (index in (numberOfChild + 1 until numberOfChildTextView)) {
                val currentTextView = holder.tasksLinearLayout.getChildAt(index) as TextView
                currentTextView.visibility = View.GONE
            }
            for (textViewIndex in 0 until numberOfChild) {
                val currentTextView = holder.tasksLinearLayout.getChildAt(textViewIndex) as TextView
                currentTextView.text = tasksList[position].tasks[textViewIndex].content
                //if(tasksList[position].tasks[textViewIndex].isFinished) {
                //    currentTextView.setCompoundDrawablesRelativeWithIntrinsicBounds()
                //}
                //currentTextView.setOnClickListener{ onClickTextView(textViewIndex, position)}
            }
            val lastTextView = holder.tasksLinearLayout.getChildAt(numberOfChild) as TextView
            lastTextView.text = "+"
            lastTextView.typeface = Typeface.DEFAULT_BOLD
            lastTextView.textSize = 18f
            lastTextView.setOnClickListener{ holder.openNewTaskActivity(tasksList[position].id)}
        }
    }

    /*private fun onClickTextView(listId: Int, cardId: Int) {
        val tasksActivityIntent = Intent(context, TasksListActivity::class.java)
        tasksActivityIntent.putExtra("groupId", groupId)
        tasksActivityIntent.putExtra("tableId", tableId)
        tasksActivityIntent.putExtra("listId", listId)
        tasksActivityIntent.putExtra("cardId", cardId)
        context.startActivity(tasksActivityIntent)
    }*/

    override fun getItemCount() = tasksList.size


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var context : Context
        var tasksLinearLayout : LinearLayout
        var tasksListNameTextView : TextView

        init {
            context = itemView.context
            tasksListNameTextView = itemView.findViewById(R.id.task_list_name)
            tasksLinearLayout = itemView.findViewById(R.id.linear_layout_tasks)
            tasksLinearLayout.visibility = View.GONE
            var maxNumberOfChilds = 0
            for (index in 0 until tasksList.size) {
                val maxSize = tasksList[index].tasks.size
                if (maxSize > maxNumberOfChilds) {
                    maxNumberOfChilds = maxSize
                }
            }
            for (indexView in 0..maxNumberOfChilds) {
                val textView = TextView(context)
                textView.id = indexView
                textView.gravity = Gravity.CENTER
                textView.setPadding(0, 20, 0, 20)
                textView.background = ContextCompat.getDrawable(context, R.drawable.background_sub_module_text)
                val layoutParams = LinearLayout.LayoutParams (
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                tasksLinearLayout.addView(textView, layoutParams)
            }
            tasksListNameTextView.setOnClickListener { onClickTasksListName() }
        }

        fun openNewTaskActivity(tasksListId: Int) {
            val newTaskActivityIntent = Intent(context, NewTaskActivity::class.java)
            newTaskActivityIntent.putExtra("groupId", groupId)
            newTaskActivityIntent.putExtra("tableId", tableId)
            newTaskActivityIntent.putExtra("listId", listId)
            newTaskActivityIntent.putExtra("cardId", cardId)
            newTaskActivityIntent.putExtra("tasksListId", tasksListId)
            context.startActivity(newTaskActivityIntent)
        }

        fun onClickTasksListName() {
            if (tasksLinearLayout.visibility == View.VISIBLE) {
                tasksLinearLayout.visibility = View.GONE
            }
            else {
                tasksLinearLayout.visibility = View.VISIBLE
            }
        }
    }

}
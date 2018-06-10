package com.romk.projectmanagmentapp.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.romk.projectmanagmentapp.Activities.NewCardActivity
import com.romk.projectmanagmentapp.Activities.TasksListActivity
import com.romk.projectmanagmentapp.Models.CardModel
import com.romk.projectmanagmentapp.Models.ListModel
import com.romk.projectmanagmentapp.R

class ListsAdapter(private val context: Context,
                   private val lists: List<ListModel>,
                   private val cards: List<List<CardModel>>,
                   private val tableId: Int,
                   private val groupId: Int) : RecyclerView.Adapter<ListsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListsAdapter.ViewHolder {
        val textView = LayoutInflater.from(parent.context).
            inflate(R.layout.view_list, parent, false) as View

        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.listNameTextView.text = lists[position].name
        val numberOfChildTextView = holder.cardsLinearLayout.childCount
        val numberOfChild = cards[position].size
        if (numberOfChild < numberOfChildTextView) {
            for (index in (numberOfChild + 1 until numberOfChildTextView)) {
                val currentTextView = holder.cardsLinearLayout.getChildAt(index) as TextView
                currentTextView.visibility = View.GONE
            }
            for (textViewIndex in 0..(numberOfChild - 1)) {
                val currentTextView = holder.cardsLinearLayout.getChildAt(textViewIndex) as TextView
                currentTextView.text = cards[position][textViewIndex].title
                currentTextView.setOnClickListener{
                    onClickTextView(
                        lists[position].id,
                        cards[position][textViewIndex].id)}
            }
            val lastTextView = holder.cardsLinearLayout.getChildAt(numberOfChild) as TextView
            lastTextView.text = "+"
            lastTextView.typeface = Typeface.DEFAULT_BOLD
            lastTextView.textSize = 18f
            lastTextView.setOnClickListener{ holder.openNewCardActivity(lists[position].id)}
        }
    }

    private fun onClickTextView(listId: Int, cardId: Int) {
        val tasksListActivityIntent = Intent(context, TasksListActivity::class.java)
        tasksListActivityIntent.putExtra("groupId", groupId)
        tasksListActivityIntent.putExtra("tableId", tableId)
        tasksListActivityIntent.putExtra("listId", listId)
        tasksListActivityIntent.putExtra("cardId", cardId)
        context.startActivity(tasksListActivityIntent)
    }

    override fun getItemCount() = lists.size


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var context : Context
        var cardsLinearLayout : LinearLayout
        var listNameTextView : TextView

        init {
            context = itemView.context
            listNameTextView = itemView.findViewById(R.id.list_name)
            cardsLinearLayout = itemView.findViewById(R.id.cards)
            cardsLinearLayout.visibility = View.GONE
            var maxNumberOfChilds = 0
            for (index in 0..(lists.size - 1)) {
                val maxSize = cards[index].size
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
                cardsLinearLayout.addView(textView, layoutParams)
            }
            listNameTextView.setOnClickListener { onClickListName() }
        }

        fun openNewCardActivity(listId: Int) {
            val newCardActivityIntent = Intent(context, NewCardActivity::class.java)
            newCardActivityIntent.putExtra("groupId", groupId)
            newCardActivityIntent.putExtra("tableId", tableId)
            newCardActivityIntent.putExtra("listId", listId)
            context.startActivity(newCardActivityIntent)
        }

        fun onClickListName() {
            if (cardsLinearLayout.visibility == View.VISIBLE) {
                cardsLinearLayout.visibility = View.GONE
            }
            else {
                cardsLinearLayout.visibility = View.VISIBLE
            }
        }
    }
}
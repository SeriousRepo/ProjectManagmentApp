package com.romk.projectmanagmentapp.Adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.romk.projectmanagmentapp.Models.CardModel
import com.romk.projectmanagmentapp.Models.ListModel
import com.romk.projectmanagmentapp.R

class ListsAdapter(private val lists: List<ListModel>, private val cards: List<List<CardModel>>) : RecyclerView.Adapter<ListsAdapter.ViewHolder>() {

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
            for (index in numberOfChild..(numberOfChildTextView - 1)) {
                val currentTextView = holder.cardsLinearLayout.getChildAt(index) as TextView
                currentTextView.visibility = View.GONE
            }
            for (textViewIndex in 0..(numberOfChild - 1)) {
                val currentTextView = holder.cardsLinearLayout.getChildAt(textViewIndex) as TextView
                currentTextView.text = cards[position][textViewIndex].title
            }
        }
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
                //textView.setOnClickListener { onClickTextView(textView)}
                val layoutParams = LinearLayout.LayoutParams (
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                cardsLinearLayout.addView(textView, layoutParams)
            }
            listNameTextView.setOnClickListener { onClickListName() }
            listNameTextView.setOnLongClickListener { onLongClickName() }
        }

        fun onClickListName() {
            if (cardsLinearLayout.visibility == View.VISIBLE) {
                cardsLinearLayout.visibility = View.GONE
            }
            else {
                cardsLinearLayout.visibility = View.VISIBLE
            }
        }

        fun onLongClickName() : Boolean {


            return true
        }
    }

}
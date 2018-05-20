package com.romk.projectmanagmentapp.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.romk.projectmanagmentapp.Adapters.ListsAdapter
import com.romk.projectmanagmentapp.Models.*
import com.romk.projectmanagmentapp.NetworkConnection.HttpGetRequestHandler
import com.romk.projectmanagmentapp.R
import org.json.JSONObject

class SingleTableActivity : AppCompatActivity() {
    private var tableId = 0
    private val listModels = mutableListOf<ListModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tableId = intent.extras.getInt("id")
        setContentView(R.layout.activity_single_table)


        getTable()
        if(tableId > 0) {
            setRecyclerView()
        }
        else {
            Toast.makeText(this, "incorect table id", Toast.LENGTH_SHORT).show()
        }
    }

    fun getTable() {
        val connection = HttpGetRequestHandler().execute(
            "http://kanban-project-management-api.herokuapp.com/v1/tables/${tableId}/lists",
            SessionModel.instance.email,
            SessionModel.instance.token
        )
        if(connection.get().first != 200) {
            Toast.makeText(this, "Couldn't download data", Toast.LENGTH_SHORT).show()
        }
        else {
            val jsonLists = connection.get().second.getJSONArray("data")
            var jsonList : JSONObject
            for (index in 0..(jsonLists.length() - 1)) {
                jsonList = jsonLists.getJSONObject(index)
                listModels.add(ListModel(jsonList.getInt("id"), jsonList.getString("name")))
            }
        }
    }

    fun setRecyclerView() {
        viewManager = LinearLayoutManager(this)

        viewAdapter = ListsAdapter(listModels)

        recyclerView = findViewById<RecyclerView>(R.id.lists_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}
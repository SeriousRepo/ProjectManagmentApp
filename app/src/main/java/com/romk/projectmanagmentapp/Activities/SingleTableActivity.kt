package com.romk.projectmanagmentapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.romk.projectmanagmentapp.Adapters.ListsAdapter
import com.romk.projectmanagmentapp.Models.*
import com.romk.projectmanagmentapp.NetworkConnection.HttpDeleteRequestHandler
import com.romk.projectmanagmentapp.NetworkConnection.HttpGetRequestHandler
import com.romk.projectmanagmentapp.R
import com.romk.projectmanagmentapp.Utils
import org.json.JSONObject

class SingleTableActivity : AppCompatActivity() {
    private var tableId = 0
    private val listModels = mutableListOf<ListModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tableId = intent.extras.getInt("tableId")
        setContentView(R.layout.activity_single_table)
        setSupportActionBar(findViewById(R.id.single_table_toolbar))


        getTable()
        if(tableId > 0) {
            setRecyclerView()
        }
        else {
            Toast.makeText(this, "incorect table id", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.single_table_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId){
        R.id.menu_add -> {
            openNewListActivity()
            true
        }
        R.id.menu_remove -> {
            removeTable()
            true
        }
        R.id.menu_logout -> {
            Utils().deleteSession(this)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
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

    private fun openNewListActivity() {
        val newListActivityIntent = Intent(this, NewListActivity::class.java)
        newListActivityIntent.putExtra("tableId", tableId)
        startActivity(newListActivityIntent)
    }

    private fun removeTable() {
        val connection = HttpDeleteRequestHandler().execute(
            "http://kanban-project-management-api.herokuapp.com/v1/tables/${tableId}",
            SessionModel.instance.email,
            SessionModel.instance.token)

        if(connection.get() == 200)
        {
            val tablesActivityIntent = Intent(this, TablesActivity::class.java)
            startActivity(tablesActivityIntent)
            Toast.makeText(this, "Successfuly removed table", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this, "Connection error, code ${connection.get()}", Toast.LENGTH_SHORT).show()
        }
    }
}
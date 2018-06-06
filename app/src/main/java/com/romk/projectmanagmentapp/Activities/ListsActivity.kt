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

class ListsActivity : AppCompatActivity() {
    private var tableId = 0
    private var groupId = 0
    private val listModels = mutableListOf<ListModel>()
    private val cardModels = mutableListOf<List<CardModel>>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tableId = intent.extras.getInt("tableId")
        groupId = intent.extras.getInt("groupId")
        setContentView(R.layout.activity_lists)
        setSupportActionBar(findViewById(R.id.lists_toolbar))

        getTable()
        getCards()
        setRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.lists_menu, menu)
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

    private fun getTable() {
        val connection = HttpGetRequestHandler().execute(
            "http://kanban-project-management-api.herokuapp.com/v1/tables/${tableId}/lists",
            SessionModel.instance.email,
            SessionModel.instance.token
        )
        if(connection.get().first != 200) {
            Toast.makeText(this, "Connection error ${connection.get().first}", Toast.LENGTH_SHORT).show()
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

    private fun getCards() {
        for (list in listModels) {
            val connection = HttpGetRequestHandler().execute(
                "http://kanban-project-management-api.herokuapp.com/v1/tables/${tableId}/lists/${list.id}/cards",
                SessionModel.instance.email,
                SessionModel.instance.token
            )
            if(connection.get().first == 200) {
                val jsonCards = connection.get().second.getJSONArray("data")
                var jsonCard : JSONObject
                val cardList = mutableListOf<CardModel>()
                for (index in 0..(jsonCards.length() - 1)) {
                    jsonCard = jsonCards.getJSONObject(index)
                    cardList.add(CardModel(jsonCard.getInt("id"),
                        jsonCard.getString("title"),
                        jsonCard.getString("description"))
                    )
                }
                cardModels.add(cardList)
            }
            else {
                Toast.makeText(this, "Connection error ${connection.get().first}", Toast.LENGTH_SHORT).show()
                break
            }
        }
    }

    private fun setRecyclerView() {
        viewManager = LinearLayoutManager(this)

        viewAdapter = ListsAdapter(listModels, cardModels, tableId, groupId)

        recyclerView = findViewById<RecyclerView>(R.id.lists_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun openNewListActivity() {
        val newListActivityIntent = Intent(this, NewListActivity::class.java)
        newListActivityIntent.putExtra("groupId", groupId)
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
            tablesActivityIntent.putExtra("groupId", groupId)
            startActivity(tablesActivityIntent)
            Toast.makeText(this, "Successfuly removed table", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this, "Connection error, code ${connection.get()}", Toast.LENGTH_SHORT).show()
        }
    }
}
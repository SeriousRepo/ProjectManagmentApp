package com.romk.projectmanagmentapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.romk.projectmanagmentapp.Adapters.TablesAdapter
import com.romk.projectmanagmentapp.Models.SessionModel
import com.romk.projectmanagmentapp.Models.TableModel
import com.romk.projectmanagmentapp.NetworkConnection.HttpDeleteRequestHandler
import com.romk.projectmanagmentapp.NetworkConnection.HttpGetRequestHandler
import com.romk.projectmanagmentapp.R
import com.romk.projectmanagmentapp.Utils
import org.json.JSONObject

class TablesActivity : AppCompatActivity() {
    private val tablesModels = mutableListOf<TableModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tables)
        setSupportActionBar(findViewById(R.id.tables_toolbar))

        getTables()
        setRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.tables_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId){
        R.id.menu_add -> {
            openNewTableActivity()
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

    private fun setRecyclerView() {
        viewManager = LinearLayoutManager(this)

        viewAdapter = TablesAdapter(this, tablesModels)

        recyclerView = findViewById<RecyclerView>(R.id.tables_view_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun getTables() {
        val connection = HttpGetRequestHandler().execute("http://kanban-project-management-api.herokuapp.com/v1/tables", SessionModel.instance.email, SessionModel.instance.token)
        if (connection.get().first == 200) {
            val jsonTables = connection.get().second.getJSONArray("data")
            var jsonTable: JSONObject
            var table: TableModel
            for (index in 0..(jsonTables.length() - 1)) {
                jsonTable = jsonTables.getJSONObject(index)
                table = TableModel(jsonTable.getInt("id"), jsonTable.getString("name"))
                tablesModels.add(table)
            }
        }
    }

    private fun openNewTableActivity() {
        val newTableActivityIntent = Intent(this, NewTableActivity::class.java)
        startActivity(newTableActivityIntent)
    }
}

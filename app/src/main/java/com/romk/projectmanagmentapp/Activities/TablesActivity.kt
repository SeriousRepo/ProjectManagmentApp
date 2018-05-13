package com.romk.projectmanagmentapp.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.romk.projectmanagmentapp.Adapters.TablesAdapter
import com.romk.projectmanagmentapp.Models.SessionModel
import com.romk.projectmanagmentapp.Models.TableModel
import com.romk.projectmanagmentapp.NetworkConnection.HttpGetRequestHandler
import com.romk.projectmanagmentapp.R
import org.json.JSONObject

class TablesActivity : AppCompatActivity() {
    private val tablesModels = mutableListOf<TableModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tables)

        getTables()
        setRecyclerView()
    }

    fun setRecyclerView() {
        viewManager = LinearLayoutManager(this)

        viewAdapter = TablesAdapter(this, tablesModels)

        recyclerView = findViewById<RecyclerView>(R.id.tables_view_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    fun getTables() {
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
}
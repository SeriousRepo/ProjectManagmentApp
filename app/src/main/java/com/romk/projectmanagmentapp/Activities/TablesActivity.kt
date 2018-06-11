package com.romk.projectmanagmentapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
    private var groupId = 0
    private val tablesModels = mutableListOf<TableModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupId = intent.extras.getInt("groupId")
        setContentView(R.layout.activity_tables)
        setSupportActionBar(findViewById(R.id.tables_toolbar))

        getTables()
        setRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.tables_menu, menu)
        menu.findItem(R.id.menu_edit).isVisible = false
        if (groupId == 0) {
            menu.findItem(R.id.menu_remove).isVisible = false
            menu.findItem(R.id.menu_show_members).isVisible = false
        }
        else {
            val connection = HttpGetRequestHandler().execute(
                "http://kanban-project-management-api.herokuapp.com/v1/groups/$groupId",
                SessionModel.instance.email,
                SessionModel.instance.token
            )
            if (connection.get().first == 200 &&
                connection.get().second
                    .getJSONObject("data")
                    .getJSONObject("group")
                    .getJSONObject("leader")
                    .getString("email") == SessionModel.instance.email) {

                menu.findItem(R.id.menu_edit).isVisible = true
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId){
        R.id.menu_add -> {
            openNewTableActivity()
            true
        }
        R.id.menu_edit -> {
            val editGroupActivityIntent = Intent(this, EditGroupActivity::class.java)
            editGroupActivityIntent.putExtra("groupId", groupId)
            startActivity(editGroupActivityIntent)
            true
        }
        R.id.menu_remove -> {
            if(groupId != 0) {
                removeGroup()
            }
            true
        }
        R.id.menu_show_members -> {
            val openGroupMembersActivityIntent = Intent(this, GroupMembersActivity::class.java)
            openGroupMembersActivityIntent.putExtra("groupId", groupId)
            startActivity(openGroupMembersActivityIntent)
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

        viewAdapter = TablesAdapter(this, tablesModels, groupId)

        recyclerView = findViewById<RecyclerView>(R.id.tables_view_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun getTables() {
        val uri: String
        if(groupId == 0) {
            uri = "http://kanban-project-management-api.herokuapp.com/v1/private_tables"
        }
        else {
            uri = "http://kanban-project-management-api.herokuapp.com/v1/${groupId}/tables"
        }
        val connection = HttpGetRequestHandler().execute(uri, SessionModel.instance.email, SessionModel.instance.token)
        if (connection.get().first == 200) {
            val jsonTables = connection.get().second.getJSONArray("data")
            var jsonTable: JSONObject
            var table: TableModel
            for (index in 0..(jsonTables.length() - 1)) {
                jsonTable = jsonTables.getJSONObject(index)
                table = TableModel(
                    jsonTable.getInt("id"),
                    jsonTable.getString("name"),
                    jsonTable.getBoolean("is_private"),
                    0
                )
                if (groupId != 0) {
                    table.groupId = jsonTable.getInt("group_id")
                }
                tablesModels.add(table)
            }
        }
    }

    private fun openNewTableActivity() {
        val newTableActivityIntent = Intent(this, NewTableActivity::class.java)
        newTableActivityIntent.putExtra("groupId", groupId)
        startActivity(newTableActivityIntent)
    }

    private fun removeGroup() {
        val connection = HttpDeleteRequestHandler().execute(
            "http://kanban-project-management-api.herokuapp.com/v1/groups/${groupId}",
            SessionModel.instance.email,
            SessionModel.instance.token)
        if(connection.get().first == 200) {
            val groupsActivityIntent = Intent(this, GroupsActivity::class.java)
            startActivity(groupsActivityIntent)
            Toast.makeText(this, "Removed group", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this, "Connection error, code ${connection.get()}", Toast.LENGTH_SHORT).show()
        }
    }
}

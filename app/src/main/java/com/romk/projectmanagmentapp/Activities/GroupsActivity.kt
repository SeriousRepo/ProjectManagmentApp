package com.romk.projectmanagmentapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.romk.projectmanagmentapp.Adapters.GroupsAdapter
import com.romk.projectmanagmentapp.Models.SessionModel
import com.romk.projectmanagmentapp.Models.SimpleGroupModel
import com.romk.projectmanagmentapp.NetworkConnection.HttpGetRequestHandler
import com.romk.projectmanagmentapp.R
import com.romk.projectmanagmentapp.Utils
import org.json.JSONObject

class GroupsActivity : AppCompatActivity() {
    private val groupModels = mutableListOf<SimpleGroupModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)
        setSupportActionBar(findViewById(R.id.groups_toolbar))

        getGroups()
        setRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.groups_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId){
        R.id.menu_add -> {
            openNewGroupActivity()
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

    fun setRecyclerView() {
        viewManager = LinearLayoutManager(this)

        viewAdapter = GroupsAdapter(this, groupModels)

        recyclerView = findViewById<RecyclerView>(R.id.groups_view_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    fun getGroups() {
        val connection = HttpGetRequestHandler().execute("http://kanban-project-management-api.herokuapp.com/v1/groups", SessionModel.instance.email, SessionModel.instance.token)
        if (connection.get().first == 200) {
            val jsonGroups = connection.get().second.getJSONArray("data")
            var jsonGroup: JSONObject
            var group: SimpleGroupModel
            for (index in 0..(jsonGroups.length() - 1)) {
                jsonGroup = jsonGroups.getJSONObject(index)
                group = SimpleGroupModel(jsonGroup.getInt("id"), jsonGroup.getInt("leader_id"), jsonGroup.getString("name"))
                groupModels.add(group)
            }
        }
    }

    private fun openNewGroupActivity() {
        val newGroupActivityIntent = Intent(this, NewGroupActivity::class.java)
        startActivity(newGroupActivityIntent)
    }
}
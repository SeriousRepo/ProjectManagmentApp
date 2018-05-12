package com.romk.projectmanagmentapp

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.romk.projectmanagmentapp.Adapters.GroupsAdapter
import com.romk.projectmanagmentapp.NetworkConnection.HttpGetRequestHandler
import org.json.JSONObject

class GroupsActivity : Activity() {

    private lateinit var userEmail: String
    private lateinit var userToken: String
    private val groupModels = mutableListOf<GroupModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userToken = intent.getStringExtra("authentication_token")
        userEmail = intent.getStringExtra("email")
        setContentView(R.layout.groups_activity)

        getGroups()
        setRecyclerView()
    }

    fun setRecyclerView() {
        viewManager = LinearLayoutManager(this)

        viewAdapter = GroupsAdapter(groupModels)

        recyclerView = findViewById<RecyclerView>(R.id.groups_view_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    fun getGroups() {
        val connection = HttpGetRequestHandler().execute("http://kanban-project-management-api.herokuapp.com/v1/groups", userEmail, userToken)
        if (connection.get().first == 200) {
            val jsonGroups = connection.get().second.getJSONArray("data")
            var jsonGroup: JSONObject
            var group: GroupModel
            for (index in 0..(jsonGroups.length() - 1)) {
                jsonGroup = jsonGroups.getJSONObject(index)
                group = GroupModel(jsonGroup.getInt("id"), jsonGroup.getInt("leader_id"), jsonGroup.getString("name"))
                groupModels.add(group)
            }
        }
    }
}
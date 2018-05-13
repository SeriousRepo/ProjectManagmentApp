package com.romk.projectmanagmentapp.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import android.widget.Toast
import com.romk.projectmanagmentapp.Adapters.GroupMembersAdapter
import com.romk.projectmanagmentapp.Models.ExtendedGroupModel
import com.romk.projectmanagmentapp.Models.SessionModel
import com.romk.projectmanagmentapp.Models.UserModel
import com.romk.projectmanagmentapp.NetworkConnection.HttpGetRequestHandler
import com.romk.projectmanagmentapp.R
import org.json.JSONObject

class SingleGroupActivity : AppCompatActivity() {

    private var groupId = 0
    private lateinit var group : ExtendedGroupModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupId = intent.extras.getInt("id")
        setContentView(R.layout.activity_single_group)


        getGroup()
        if(groupId > 0) {
            setRecyclerView()
            setView()
        }
        else {
            Toast.makeText(this, "incorect group id", Toast.LENGTH_SHORT).show()
        }
    }

    fun getGroup() {
        val connection = HttpGetRequestHandler().execute(
            "http://kanban-project-management-api.herokuapp.com/v1/groups/${groupId}",
            SessionModel.instance.email,
            SessionModel.instance.token
        )
        if(connection.get().first != 200) {
            Toast.makeText(this, "Couldn't download data", Toast.LENGTH_SHORT).show()
        }
        else {
            val jsonGroup = connection.get().second.getJSONObject("data").getJSONObject("group")
            val jsonLeader = jsonGroup.getJSONObject("leader")
            val leader = UserModel(jsonLeader.getInt("id"),  jsonLeader.getString("email"))
            val jsonMembers = jsonGroup.getJSONArray("members")
            val members = mutableListOf<UserModel>()
            var jsonMember : JSONObject
            for (index in 0..(jsonMembers.length() - 1)) {
                jsonMember = jsonMembers.getJSONObject(index)
                members.add(UserModel(jsonMember.getInt("id"), jsonMember.getString("email")))
            }
            group = ExtendedGroupModel(jsonGroup.getInt("id"),
                                       jsonGroup.getString("name"),
                                       leader,
                                       members
            )
        }
    }

    fun setRecyclerView() {
        viewManager = LinearLayoutManager(this)

        viewAdapter = GroupMembersAdapter(group)

        recyclerView = findViewById<RecyclerView>(R.id.members_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    fun setView() {
        findViewById<TextView>(R.id.group_name).text = group.groupName
    }
}
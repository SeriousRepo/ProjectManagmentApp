package com.romk.projectmanagmentapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.romk.projectmanagmentapp.Adapters.GroupMembersAdapter
import com.romk.projectmanagmentapp.Models.ExtendedGroupModel
import com.romk.projectmanagmentapp.Models.SessionModel
import com.romk.projectmanagmentapp.Models.UserModel
import com.romk.projectmanagmentapp.NetworkConnection.HttpGetRequestHandler
import com.romk.projectmanagmentapp.R
import com.romk.projectmanagmentapp.Utils
import org.json.JSONObject

class GroupMembersActivity : AppCompatActivity() {

    private var groupId = 0
    private lateinit var group : ExtendedGroupModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupId = intent.extras.getInt("groupId")
        setContentView(R.layout.activity_group_members)
        setSupportActionBar(findViewById(R.id.group_members_toolbar))

        getGroup()
        setRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.group_members_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId){
        R.id.menu_add -> {
            openNewMemberActivity()
            true
        }
        R.id.menu_remove -> {
            removeMember()
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

    private fun getGroup() {
        val connection = HttpGetRequestHandler().execute(
            "http://kanban-project-management-api.herokuapp.com/v1/groups/${groupId}",
            SessionModel.instance.email,
            SessionModel.instance.token
        )
        if(connection.get().first != 200) {
            Toast.makeText(this, "Connection error, code ${connection.get().first}", Toast.LENGTH_SHORT).show()
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

    private fun setRecyclerView() {
        viewManager = LinearLayoutManager(this)

        viewAdapter = GroupMembersAdapter(group)

        recyclerView = findViewById<RecyclerView>(R.id.members_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun openNewMemberActivity() {
        val newMemberActivity = Intent(this, NewMemberActivity::class.java)
        newMemberActivity.putExtra("groupId", groupId)
        startActivity(newMemberActivity)
    }

    private fun removeMember() {
        val deleteFromGroupMemberActivity = Intent(this, DeleteGroupMemberActivity::class.java)
        val memberIds = arrayListOf<Int>()
        val memberEmails = arrayListOf<String>()
        for (member in group.members) {
            memberIds.add(member.id)
            memberEmails.add(member.email)
        }
        deleteFromGroupMemberActivity.putExtra("memberIds", memberIds)
        deleteFromGroupMemberActivity.putExtra("memberEmails", memberEmails)
        deleteFromGroupMemberActivity.putExtra("leaderId", group.leader.id)
        deleteFromGroupMemberActivity.putExtra("groupId", groupId)
        startActivity(deleteFromGroupMemberActivity)
    }
}
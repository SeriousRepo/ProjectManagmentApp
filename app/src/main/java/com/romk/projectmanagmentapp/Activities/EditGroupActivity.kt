package com.romk.projectmanagmentapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.romk.projectmanagmentapp.Adapters.EditGroupAdapter
import com.romk.projectmanagmentapp.Models.ExtendedGroupModel
import com.romk.projectmanagmentapp.Models.SessionModel
import com.romk.projectmanagmentapp.Models.UserModel
import com.romk.projectmanagmentapp.NetworkConnection.HttpGetRequestHandler
import com.romk.projectmanagmentapp.NetworkConnection.HttpPutRequestHandler
import com.romk.projectmanagmentapp.R
import com.romk.projectmanagmentapp.Utils
import org.json.JSONObject

class EditGroupActivity : AppCompatActivity() {
    private var name = ""
    private var newLeaderPosition = 0
    private var groupId = 0
    private lateinit var group: ExtendedGroupModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupId = intent.extras.getInt("groupId")
        setContentView(R.layout.activity_edit_group)
        setSupportActionBar(findViewById(R.id.edit_group_toolbar))

        getGroup()
        setLayout()
        bindButtons()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.basic_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId){
        R.id.menu_logout -> {
            Utils().deleteSession(this)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun setGroupProperties() {
        name = findViewById<EditText>(R.id.edit_group_name_edit_text).text.toString()
        if ((viewAdapter as EditGroupAdapter).getSelectedItem() != -1) {
            newLeaderPosition = (viewAdapter as EditGroupAdapter).getSelectedItem()
        }
        else {
            Toast.makeText(this, "Something is really wrong!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLayout() {
        findViewById<EditText>(R.id.edit_group_name_edit_text).hint = group.groupName
        setRecyclerView()
    }

    private fun editGroup() {
        val connection = HttpPutRequestHandler().execute(
            "http://kanban-project-management-api.herokuapp.com/v1/groups/$groupId",
            getJsonString(),
            SessionModel.instance.email,
            SessionModel.instance.token
        )
        if (connection.get().first == 200) {
            val tablesActivityIntent = Intent(this, TablesActivity::class.java)
            tablesActivityIntent.putExtra("groupId", groupId)
            startActivity(tablesActivityIntent)
        }
        else {
            Toast.makeText(this, "Connection error, code ${connection.get().first}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getJsonString(): String {
        val json = JSONObject().put("name", name)
        if(name == "") {
            json.put("name", group.groupName)
        }
        json.put("leader_id", group.members[newLeaderPosition].id)
        return json.toString()
    }

    private fun getGroup() {
        val connection = HttpGetRequestHandler().execute(
            "http://kanban-project-management-api.herokuapp.com/v1/groups/$groupId",
            SessionModel.instance.email,
            SessionModel.instance.token
        )
        if (connection.get().first == 200) {
            val groupJson = connection.get().second.getJSONObject("data").getJSONObject("group")
            val membersJson = groupJson.getJSONArray("members")
            val leaderJson = groupJson.getJSONObject("leader")
            val leader = UserModel(leaderJson.getInt("id"), leaderJson.getString("email"))
            val members = mutableListOf<UserModel>()
            for (i in 0 until membersJson.length()) {
                val memberJson = membersJson.getJSONObject(i)
                members.add(UserModel(memberJson.getInt("id"), memberJson.getString("email")))
            }
            group = ExtendedGroupModel(
                groupJson.getInt("id"),
                groupJson.getString("name"),
                leader,
                members
            )
        }
        else {
            Toast.makeText(this, "Connection error, code ${connection.get().first}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindButtons() {
        val createButton = findViewById<Button>(R.id.button_edit_group)
        createButton.setOnClickListener {
            setGroupProperties()
            if(name == "" && group.leader.id == group.members[newLeaderPosition].id) {
                Toast.makeText(this, "No changes applied", Toast.LENGTH_SHORT).show()
            }
            else {
                editGroup()
            }
        }
    }

    private fun setRecyclerView() {
        viewManager = LinearLayoutManager(this)

        var leaderPos = -1

        for (i in 0 until group.members.size) {
            if (group.members[i].id == group.leader.id) {
                leaderPos = i
            }
        }

        viewAdapter = EditGroupAdapter(this, group.members, leaderPos)

        recyclerView = findViewById<RecyclerView>(R.id.edit_group_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}
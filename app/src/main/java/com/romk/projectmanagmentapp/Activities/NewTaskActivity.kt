package com.romk.projectmanagmentapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.romk.projectmanagmentapp.Adapters.GroupMembersAdapter
import com.romk.projectmanagmentapp.Adapters.NewTaskAdapter
import com.romk.projectmanagmentapp.Models.SessionModel
import com.romk.projectmanagmentapp.Models.UserModel
import com.romk.projectmanagmentapp.NetworkConnection.HttpGetRequestHandler
import com.romk.projectmanagmentapp.NetworkConnection.HttpPostRequestHandler
import com.romk.projectmanagmentapp.R
import org.json.JSONObject

class NewTaskActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    var groupId = 0
    var tableId = 0
    var listId = 0
    var cardId = 0
    var tasksListId = 0
    private lateinit var content : String
    private var lastChecked = -1
    private var users = mutableListOf<UserModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupId = intent.extras.getInt("groupId")
        tableId = intent.extras.getInt("tableId")
        listId = intent.extras.getInt("listId")
        cardId = intent.extras.getInt("cardId")
        tasksListId = intent.extras.getInt("tasksListId")
        setContentView(R.layout.activity_new_task)
        setSupportActionBar(findViewById(R.id.new_task_toolbar))

        getUsers()
        setRecyclerView()
        bindButtons()
    }

    private fun setTaskProperties() {
        lastChecked = (viewAdapter as NewTaskAdapter).getSelectedItem()
        content = findViewById<EditText>(R.id.new_task_content_edit_text).text.toString()
    }

    private fun createTask() {
        val connection = HttpPostRequestHandler().execute(
            "http://kanban-project-management-api.herokuapp.com/v1/tables/$tableId/lists/$listId/cards/$cardId/tasks_lists/$tasksListId/tasks",
            getJsonString(),
            SessionModel.instance.email,
            SessionModel.instance.token
        )
        if (connection.get().first == 201) {
            val tasksListActivityIntent = Intent(this, TasksListActivity::class.java)
            tasksListActivityIntent.putExtra("groupId", groupId)
            tasksListActivityIntent.putExtra("tableId", tableId)
            tasksListActivityIntent.putExtra("listId", listId)
            tasksListActivityIntent.putExtra("cardId", cardId)
            startActivity(tasksListActivityIntent)
        }
        else {
            Toast.makeText(this, "Connection error, code ${connection.get().first}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getJsonString(): String {
        val json = JSONObject().put("content", content)
                               .put("assigned_to", users[lastChecked].id)
        return json.toString()
    }

    private fun bindButtons() {
        val createButton = findViewById<Button>(R.id.button_create_task)
        createButton.setOnClickListener {
            setTaskProperties()
            if (content.isEmpty()) {
                Toast.makeText(this, "Content of task can not be empty.", Toast.LENGTH_SHORT).show()
            }
            else if (lastChecked == -1) {
                Toast.makeText(this, "Group member can not be empty", Toast.LENGTH_SHORT).show()
            }
            else {
                createTask()
            }
        }
    }

    private fun getUsers() {
        val connection = HttpGetRequestHandler().execute(
            "http://kanban-project-management-api.herokuapp.com/v1/groups/$groupId",
            SessionModel.instance.email,
            SessionModel.instance.token
        )
        if (connection.get().first == 200) {
            val jsonGroup = connection.get().second.getJSONObject("data").getJSONObject("group")
            val jsonMembers = jsonGroup.getJSONArray("members")
            var jsonMember : JSONObject
            for (index in 0 until jsonMembers.length()) {
                jsonMember = jsonMembers.getJSONObject(index)
                users.add(UserModel(jsonMember.getInt("id"), jsonMember.getString("email")))
            }
        }
        else {
            Toast.makeText(this, "Connection error, code ${connection.get().first}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setRecyclerView() {
        viewManager = LinearLayoutManager(this)

        viewAdapter = NewTaskAdapter(users)

        recyclerView = findViewById<RecyclerView>(R.id.users_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}

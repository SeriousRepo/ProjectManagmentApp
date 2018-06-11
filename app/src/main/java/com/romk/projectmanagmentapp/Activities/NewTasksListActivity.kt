package com.romk.projectmanagmentapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.romk.projectmanagmentapp.Models.SessionModel
import com.romk.projectmanagmentapp.NetworkConnection.HttpPostRequestHandler
import com.romk.projectmanagmentapp.R
import org.json.JSONObject

class NewTasksListActivity: AppCompatActivity() {
    private lateinit var name : String
    private var groupId = 0
    private var tableId = 0
    private var listId = 0
    private var cardId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_tasks_list)
        groupId = intent.extras.getInt("groupId")
        tableId = intent.extras.getInt("tableId")
        listId = intent.extras.getInt("listId")
        cardId = intent.extras.getInt("cardId")


        bindButtons()
    }

    private fun bindButtons() {
        val createButton = findViewById<Button>(R.id.button_create_tasks_list)
        createButton.setOnClickListener {
            setTasksListProperties()
            if (name.isEmpty())
            {
                Toast.makeText(this, "Name of tasks list can not be empty.", Toast.LENGTH_SHORT).show()
            }
            else {
                createTasksList()
            }
        }
    }

    private fun setTasksListProperties() {
        name = findViewById<EditText>(R.id.new_tasks_list_name_edit_text).text.toString()
    }

    private fun getJsonString() : String {
        val json = JSONObject().put("name", name)
        return json.toString()
    }

    private fun createTasksList() {
        val connection = HttpPostRequestHandler().execute(
            "http://kanban-project-management-api.herokuapp.com/v1/tables/$tableId/lists/$listId/cards/$cardId/tasks_lists",
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
            tasksListActivityIntent.putExtra("cardName", "")
            startActivity(tasksListActivityIntent)
        }
        else {
            Toast.makeText(this, "Connection error, code ${connection.get().first}", Toast.LENGTH_SHORT).show()
        }
    }
}
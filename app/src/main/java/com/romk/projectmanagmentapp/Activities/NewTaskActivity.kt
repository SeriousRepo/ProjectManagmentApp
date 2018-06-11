package com.romk.projectmanagmentapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.romk.projectmanagmentapp.Models.SessionModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupId = intent.extras.getInt("groupId")
        tableId = intent.extras.getInt("tableId")
        listId = intent.extras.getInt("listId")
        cardId = intent.extras.getInt("cardId")
        tasksListId = intent.extras.getInt("tasksListId")
        setContentView(R.layout.activity_new_task)
        setSupportActionBar(findViewById(R.id.new_task_toolbar))

        bindButtons()
    }

    private fun setTaskProperties() {
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
            tasksListActivityIntent.putExtra("cardName", "")
            startActivity(tasksListActivityIntent)
        }
        else {
            Toast.makeText(this, "Connection error, code ${connection.get().first}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getJsonString(): String {
        val json = JSONObject().put("content", content)
        return json.toString()
    }

    private fun bindButtons() {
        val createButton = findViewById<Button>(R.id.button_create_task)
        createButton.setOnClickListener {
            setTaskProperties()
            if (content.isEmpty()) {
                Toast.makeText(this, "Content of task can not be empty.", Toast.LENGTH_SHORT).show()
            }
            else {
                createTask()
            }
        }
    }
}

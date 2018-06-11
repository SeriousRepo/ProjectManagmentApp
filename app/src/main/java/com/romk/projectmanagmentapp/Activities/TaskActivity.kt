package com.romk.projectmanagmentapp.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.romk.projectmanagmentapp.Models.SessionModel
import com.romk.projectmanagmentapp.Models.TaskModel
import com.romk.projectmanagmentapp.Models.UserModel
import com.romk.projectmanagmentapp.NetworkConnection.HttpDeleteRequestHandler
import com.romk.projectmanagmentapp.NetworkConnection.HttpGetRequestHandler
import com.romk.projectmanagmentapp.NetworkConnection.HttpPutRequestHandler
import com.romk.projectmanagmentapp.R
import com.romk.projectmanagmentapp.Utils

class TaskActivity : AppCompatActivity() {
    private lateinit var task: TaskModel
    private var groupId = 0
    private var tableId = 0
    private var listId = 0
    private var cardId = 0
    private var tasksListId = 0
    private var taskID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupId = intent.extras.getInt("groupId")
        tableId = intent.extras.getInt("tableId")
        listId = intent.extras.getInt("listId")
        cardId = intent.extras.getInt("cardId")
        tasksListId = intent.extras.getInt("tasksListId")
        taskID = intent.extras.getInt("taskId")
        setContentView(R.layout.activity_task)
        setSupportActionBar(findViewById(R.id.task_toolbar))
        getTask()
        setLayout()
        bindButtons()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.task_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_remove -> {
            removeCard()
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

    private fun getTask() {
        val connection = HttpGetRequestHandler().execute(

            "http://kanban-project-management-api.herokuapp.com/v1/tables/$tableId/lists/$listId/cards/$cardId/tasks_lists/$tasksListId/tasks/$taskID",
            SessionModel.instance.email,
            SessionModel.instance.token
        )
        if (connection.get().first == 200) {
            val jsonTask = connection.get().second.getJSONObject("data").getJSONObject("task")
            val assignedToJson = jsonTask.getJSONObject("assigned_to")
            val assignedTo = UserModel(assignedToJson.getInt("id"), assignedToJson.getString("email"))
            task = TaskModel(
                jsonTask.getInt("id"),
                jsonTask.getString("content"),
                jsonTask.getBoolean("is_finished"),
                assignedTo
            )
        }
        else {
            Toast.makeText(this, "Connection error, code ${connection.get()}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindButtons() {
        val doneButton = findViewById<Button>(R.id.done_button)
        doneButton.setOnClickListener {
            val connection = HttpPutRequestHandler().execute(
                "http://kanban-project-management-api.herokuapp.com/v1/tables/$tableId/lists/$listId/cards/$cardId/tasks_lists/$tasksListId/tasks/$taskID/mark_as_finished",
                String(),
                SessionModel.instance.email,
                SessionModel.instance.token
            )
            if (connection.get().first == 200) {
                doneButton.visibility = View.GONE
                findViewById<TextView>(R.id.done_text_view).visibility = View.VISIBLE
            }
            else {
                Toast.makeText(this, "Connection error, code ${connection.get().first}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setLayout() {
        findViewById<TextView>(R.id.email_text_view).text = task.assignedTo.email
        findViewById<TextView>(R.id.content).text = task.content
        if(task.isFinished) {
            findViewById<Button>(R.id.done_button).visibility = View.GONE
            findViewById<TextView>(R.id.done_text_view).visibility = View.VISIBLE
        }
    }

    private fun removeCard() {
        val connection = HttpDeleteRequestHandler().execute(
            "http://kanban-project-management-api.herokuapp.com/v1/tables/$tableId/lists/$listId/cards/$cardId/tasks_lists/$tasksListId/tasks/$taskID",
            SessionModel.instance.email,
            SessionModel.instance.token)
        if(connection.get().first == 200) {
            val tasksListActivityIntent = Intent(this, TasksListActivity::class.java)
            tasksListActivityIntent.putExtra("groupId", groupId)
            tasksListActivityIntent.putExtra("tableId", tableId)
            tasksListActivityIntent.putExtra("listId", listId)
            tasksListActivityIntent.putExtra("cardId", cardId)
            tasksListActivityIntent.putExtra("cardName", "")
            startActivity(tasksListActivityIntent)
            Toast.makeText(this, "Removed task", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this, "Connection error, code ${connection.get()}", Toast.LENGTH_SHORT).show()
        }
    }
}

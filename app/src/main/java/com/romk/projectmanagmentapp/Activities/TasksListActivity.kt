package com.romk.projectmanagmentapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.romk.projectmanagmentapp.Adapters.TasksListAdapter
import com.romk.projectmanagmentapp.Models.SessionModel
import com.romk.projectmanagmentapp.Models.TaskModel
import com.romk.projectmanagmentapp.Models.TasksModel
import com.romk.projectmanagmentapp.Models.UserModel
import com.romk.projectmanagmentapp.NetworkConnection.HttpDeleteRequestHandler
import com.romk.projectmanagmentapp.NetworkConnection.HttpGetRequestHandler
import com.romk.projectmanagmentapp.R
import com.romk.projectmanagmentapp.Utils
import org.json.JSONObject

class TasksListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var tableId = 0
    private var groupId = 0
    private var listId = 0
    private var cardId = 0
    private var cardName = ""
    private val tasksModels = mutableListOf<TasksModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tableId = intent.extras.getInt("tableId")
        groupId = intent.extras.getInt("groupId")
        listId = intent.extras.getInt("listId")
        cardId = intent.extras.getInt("cardId")
        cardName = intent.extras.getString("cardName")
        setContentView(R.layout.activity_tasks_list)
        setSupportActionBar(findViewById(R.id.tasks_list_toolbar))

        val ids = getListOfIds()
        getTasks(ids)
        setRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.tasks_list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId){
        R.id.menu_add -> {
            openNewTasksListActivity()
            true
        }
        R.id.menu_remove -> {
            removeCard()
            true
        }
        R.id.menu_edit -> {
            val editCardActivityIntent = Intent(this, EditCardActivity::class.java)
            editCardActivityIntent.putExtra("groupId", groupId)
            editCardActivityIntent.putExtra("tableId", tableId)
            editCardActivityIntent.putExtra("listId", listId)
            editCardActivityIntent.putExtra("cardId", cardId)
            editCardActivityIntent.putExtra("cardName", cardName)
            startActivity(editCardActivityIntent)
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

    private fun getListOfIds(): List<Int> {
        val ids = mutableListOf<Int>()
        val connection = HttpGetRequestHandler().execute(
            "http://kanban-project-management-api.herokuapp.com/v1/tables/${tableId}/lists/${listId}/cards/${cardId}/tasks_lists",
            SessionModel.instance.email,
            SessionModel.instance.token
        )
        if (connection.get().first == 200) {
            val tasksList = connection.get().second.getJSONArray("data")
            for (i in 0 until tasksList.length()) {
                ids.add(tasksList.getJSONObject(i).get("id") as Int)
            }
        }
        else {
            Toast.makeText(this, "Connection error, code ${connection.get().first}", Toast.LENGTH_SHORT).show()
        }
        return ids
    }

    private fun getTasks(ids: List<Int>) {
        for (id in ids) {
            val connection = HttpGetRequestHandler().execute(
                "http://kanban-project-management-api.herokuapp.com/v1/tables/$tableId/lists/$listId/cards/$cardId/tasks_lists/$id",
                SessionModel.instance.email,
                SessionModel.instance.token
            )
            if (connection.get().first == 200) {
                val tasksListJson = connection.get().second.getJSONObject("data").getJSONObject("task_list")
                val name = tasksListJson.get("name") as String
                val jsonList = tasksListJson.getJSONArray("tasks")
                val tasks = mutableListOf<TaskModel>()
                var content: String
                var taskId: Int
                var isFinished: Boolean
                var assignedTo: UserModel
                var userJson: JSONObject
                var taskJson: JSONObject
                for (i in 0 until jsonList.length()) {
                    taskJson = jsonList.getJSONObject(i)
                    taskId = taskJson.getInt("id")
                    content = taskJson.getString("content")
                    isFinished = taskJson.getBoolean("is_finished")
                    userJson = taskJson.getJSONObject("assigned_to")
                    assignedTo = UserModel(userJson.getInt("id"), userJson.getString("email"))
                    tasks.add(TaskModel(taskId, content, isFinished, assignedTo))
                }
                tasksModels.add(TasksModel(id, name, tasks))
            }
            else {
                Toast.makeText(this, "Connection error, code ${connection.get().first}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setRecyclerView() {
        viewManager = LinearLayoutManager(this)

        viewAdapter = TasksListAdapter(this, tasksModels, groupId, tableId, listId, cardId)

        recyclerView = findViewById<RecyclerView>(R.id.tasks_list_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun openNewTasksListActivity() {
        val newTasksListActivityIntent = Intent(this, NewTasksListActivity::class.java)
        newTasksListActivityIntent.putExtra("groupId", groupId)
        newTasksListActivityIntent.putExtra("tableId", tableId)
        newTasksListActivityIntent.putExtra("listId", listId)
        newTasksListActivityIntent.putExtra("cardId", cardId)
        startActivity(newTasksListActivityIntent)
    }

    private fun removeCard() {
        val connection = HttpDeleteRequestHandler().execute(
            "http://kanban-project-management-api.herokuapp.com/v1/tables/$tableId/lists/$listId/cards/$cardId",
            SessionModel.instance.email,
            SessionModel.instance.token)

        if(connection.get().first == 200)
        {
            val listsActivityIntent = Intent(this, ListsActivity::class.java)
            listsActivityIntent.putExtra("groupId", groupId)
            listsActivityIntent.putExtra("tableId", tableId)
            startActivity(listsActivityIntent)
            Toast.makeText(this, "Successfuly removed card", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this, "Connection error, code ${connection.get()}", Toast.LENGTH_SHORT).show()
        }
    }
}
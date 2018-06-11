package com.romk.projectmanagmentapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.romk.projectmanagmentapp.Models.SessionModel
import com.romk.projectmanagmentapp.NetworkConnection.HttpGetRequestHandler
import com.romk.projectmanagmentapp.NetworkConnection.HttpPutRequestHandler
import com.romk.projectmanagmentapp.R
import com.romk.projectmanagmentapp.Utils
import org.json.JSONObject

class EditTableActivity : AppCompatActivity() {
    private var previousName = ""
    private var groupId = 0
    private var tableId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupId = intent.extras.getInt("groupId")
        tableId = intent.extras.getInt("tableId")
        setContentView(R.layout.activity_edit_table)
        setSupportActionBar(findViewById(R.id.edit_table_toolbar))

        getTable()
        bindButton()
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

    private fun getTable() {
        val connection = HttpGetRequestHandler().execute(
            "http://kanban-project-management-api.herokuapp.com/v1/tables/$tableId",
            SessionModel.instance.email,
            SessionModel.instance.token
        )
        if(connection.get().first == 200) {
            previousName = connection.get().second
                .getJSONObject("data")
                .getJSONObject("table")
                .getString("name")
            findViewById<EditText>(R.id.edit_table_name_edit_text).hint = previousName
        }
        else {
            findViewById<EditText>(R.id.edit_table_name_edit_text).hint = "new name"
        }
    }

    private fun bindButton() {
        val editTableButton = findViewById<Button>(R.id.button_edit_table)
        editTableButton.setOnClickListener {
            val newName = findViewById<EditText>(R.id.edit_table_name_edit_text).text.toString()

            val connection = HttpPutRequestHandler().execute(
                "http://kanban-project-management-api.herokuapp.com/v1/tables/$tableId",
                getJsonString(newName),
                SessionModel.instance.email,
                SessionModel.instance.token
            )
            if(connection.get().first == 200) {
                val tablesActivityIntent = Intent(this, TablesActivity::class.java)
                tablesActivityIntent.putExtra("groupId", groupId)
                tablesActivityIntent.putExtra("tableId", tableId)
                startActivity(tablesActivityIntent)
            }
            else {
                Toast.makeText(this, "Connection error ${connection.get().first}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getJsonString(newName: String): String {
        val json = JSONObject().put("name", newName)
        return json.toString()
    }
}
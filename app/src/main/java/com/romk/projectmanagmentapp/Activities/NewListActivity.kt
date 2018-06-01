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

class NewListActivity : AppCompatActivity() {
    private lateinit var name : String
    var tableId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_list)
        tableId = intent.extras.getInt("tableId")

        bindButtons()
    }

    private fun bindButtons() {
        val createButton = findViewById<Button>(R.id.button_create_list)
        createButton.setOnClickListener {
            setTableProperties()
            if (name.isEmpty())
            {
                Toast.makeText(this, "Name of list can not be empty.", Toast.LENGTH_SHORT).show()
            }
            createList()
        }
    }

    private fun setTableProperties() {
        name = findViewById<EditText>(R.id.new_list_name_edit_text).text.toString()
    }

    private fun getJsonString() : String {
        val json = JSONObject().put("name", name)
        return json.toString()
    }

    private fun createList() {
        val connection = HttpPostRequestHandler().execute("http://kanban-project-management-api.herokuapp.com/v1/tables/${tableId}/lists",
            getJsonString(),
            SessionModel.instance.email,
            SessionModel.instance.token
        )
        if (connection.get().first == 201) {
            val singleTableActivityIntent = Intent(this, SingleTableActivity::class.java)
            singleTableActivityIntent.putExtra("tableId", tableId)
            startActivity(singleTableActivityIntent)
        }
        else {
            Toast.makeText(this, "Connection error, code ${connection.get().first}", Toast.LENGTH_SHORT).show()
        }
    }
}
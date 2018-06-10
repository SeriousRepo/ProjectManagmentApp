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

class NewGroupActivity : AppCompatActivity() {
    private lateinit var name : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_group)

        bindButtons()
    }

    private fun setGroupProperties() {
        name = findViewById<EditText>(R.id.new_group_name_edit_text).text.toString()
    }

    private fun createGroup() {
        val connection = HttpPostRequestHandler().execute("http://kanban-project-management-api.herokuapp.com/v1/groups",
            getJsonString(),
            SessionModel.instance.email,
            SessionModel.instance.token
        )
        if (connection.get().first == 200) {
            val groupsActivityIntent = Intent(this, GroupsActivity::class.java)
            startActivity(groupsActivityIntent)
        }
        else {
            Toast.makeText(this, "Connection error, code ${connection.get().first}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getJsonString(): String {
        val json = JSONObject().put("name", name)
        return json.toString()
    }

    private fun bindButtons() {
        val createButton = findViewById<Button>(R.id.button_create_group)
        createButton.setOnClickListener {
            setGroupProperties()
            if (name.isEmpty())
            {
                Toast.makeText(this, "Name of group can not be empty.", Toast.LENGTH_SHORT).show()
            }
            else {
                createGroup()
            }
        }
    }
}
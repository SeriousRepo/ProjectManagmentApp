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
import com.romk.projectmanagmentapp.NetworkConnection.HttpPostRequestHandler
import com.romk.projectmanagmentapp.R
import com.romk.projectmanagmentapp.Utils
import org.json.JSONObject

class NewMemberActivity : AppCompatActivity() {
    private lateinit var email : String
    private var groupId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_member)
        setSupportActionBar(findViewById(R.id.new_member_toolbar))
        groupId = intent.extras.getInt("groupId")

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

    private fun bindButtons() {
        val createButton = findViewById<Button>(R.id.button_add_member)
        createButton.setOnClickListener {
            setTableProperties()
            if (email.isEmpty())
            {
                Toast.makeText(this, "Email can not be empty.", Toast.LENGTH_SHORT).show()
            }
            else {
                addMember()
            }
        }
    }

    private fun setTableProperties() {
        email = findViewById<EditText>(R.id.new_member_email_edit_text).text.toString()
    }

    private fun getJsonString() : String {
        val json = JSONObject().put("email", email)
        return json.toString()
    }

    private fun addMember() {
        val connection = HttpPostRequestHandler().execute(
            "http://kanban-project-management-api.herokuapp.com/v2/groups/$groupId/add_user_to_group",
            getJsonString(),
            SessionModel.instance.email,
            SessionModel.instance.token
        )
        if (connection.get().first == 200) {
            val groupMembersActivityIntent = Intent(this, GroupMembersActivity::class.java)
            groupMembersActivityIntent.putExtra("groupId", groupId)
            startActivity(groupMembersActivityIntent)
        }
        else {
            Toast.makeText(this, "Connection error, code ${connection.get().first}", Toast.LENGTH_SHORT).show()
        }
    }
}
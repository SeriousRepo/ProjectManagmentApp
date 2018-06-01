package com.romk.projectmanagmentapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.romk.projectmanagmentapp.Models.SessionModel
import com.romk.projectmanagmentapp.NetworkConnection.HttpPostRequestHandler
import com.romk.projectmanagmentapp.R
import org.json.JSONObject

class NewTableActivity : AppCompatActivity() {
    private var privacy = true
    private lateinit var name : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_table)

        bindButtons()
    }

    private fun setTableProperties() {
        name = findViewById<EditText>(R.id.new_table_name_edit_text).text.toString()
        val checkBox = findViewById<CheckBox>(R.id.check_box_privacy)
        privacy = checkBox.isChecked
    }

    private fun createTable() {
        val connection = HttpPostRequestHandler().execute("http://kanban-project-management-api.herokuapp.com/v1/tables",
                getJsonString(),
                SessionModel.instance.email,
                SessionModel.instance.token
        )
        if (connection.get().first == 201) {
            val tablesActivityIntent = Intent(this, TablesActivity::class.java)
            startActivity(tablesActivityIntent)
        }
        else {
            Toast.makeText(this, "Connection error, code ${connection.get().first}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getJsonString(): String {
        val json = JSONObject().put("name", name)
                               .put("is_private", privacy)
        return json.toString()
    }

    private fun bindButtons() {
        val createButton = findViewById<Button>(R.id.button_create_tabble)
        createButton.setOnClickListener {
            setTableProperties()
            if (name.isEmpty())
            {
                Toast.makeText(this, "Name of table can not be empty.", Toast.LENGTH_SHORT).show()
            }
            createTable()
        }
    }
}

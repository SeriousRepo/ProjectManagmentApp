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

class EditCardActivity : AppCompatActivity() {
    private var groupId: Int = 0
    private var tableId: Int = 0
    private var listId: Int = 0
    private var cardId = 0
    private var previousName = ""
    private var newName = ""
    private var newDescription = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupId = intent.extras.getInt("groupId")
        tableId = intent.extras.getInt("tableId")
        listId = intent.extras.getInt("listId")
        cardId = intent.extras.getInt("cardId")
        previousName = intent.extras.getString("cardName")
        setContentView(R.layout.activity_edit_card)
        setSupportActionBar(findViewById(R.id.edit_card_toolbar))
        bindButtons()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.basic_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_logout -> {
            Utils().deleteSession(this)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun bindButtons() {
        findViewById<EditText>(R.id.edit_card_name_edit_text).hint = previousName
        val createCardButton = findViewById<Button>(R.id.button_edit_card)
        createCardButton.setOnClickListener {
            newName = findViewById<EditText>(R.id.edit_card_name_edit_text).text.toString()
            newDescription = findViewById<EditText>(R.id.edit_card_description_edit_text).text.toString()
            if (previousName == newName || (newDescription == "" && newName == "")) {
                Toast.makeText(this, "You have to change something.", Toast.LENGTH_SHORT).show()
            } else {
                editCard()
            }
        }
    }

    private fun getJson(): String {
        val json = JSONObject().put("title", newName)
            .put("description", newDescription)
        return json.toString()
    }

    private fun editCard() {
        val connection = HttpPutRequestHandler().execute(
            "http://kanban-project-management-api.herokuapp.com/v1/tables/${tableId}/lists/${listId}/cards/$cardId",
            getJson(),
            SessionModel.instance.email,
            SessionModel.instance.token
        )
        if (connection.get().first == 200) {
            val listsActivityIntent = Intent(this, ListsActivity::class.java)
            listsActivityIntent.putExtra("groupId", groupId)
            listsActivityIntent.putExtra("tableId", tableId)
            startActivity(listsActivityIntent)

        } else {
            Toast.makeText(this, "Connection error, code ${connection.get().first}", Toast.LENGTH_SHORT).show()
        }
    }
}
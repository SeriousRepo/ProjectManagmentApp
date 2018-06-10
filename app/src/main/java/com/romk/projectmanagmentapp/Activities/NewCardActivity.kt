package com.romk.projectmanagmentapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.romk.projectmanagmentapp.Models.CardModel
import com.romk.projectmanagmentapp.Models.SessionModel
import com.romk.projectmanagmentapp.NetworkConnection.HttpPostRequestHandler
import com.romk.projectmanagmentapp.R
import org.json.JSONObject

class NewCardActivity : AppCompatActivity() {
    private var groupId: Int = 0
    private var tableId: Int = 0
    private var listId: Int = 0
    private lateinit var cardModel: CardModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupId = intent.extras.getInt("groupId")
        tableId = intent.extras.getInt("tableId")
        listId = intent.extras.getInt("listId")
        setContentView(R.layout.activity_new_card)
        setSupportActionBar(findViewById(R.id.new_card_toolbar))
        bindButtons()
    }

    //TODO add suport to action bar

    private fun bindButtons() {
        val createCardButton = findViewById<Button>(R.id.button_create_card)
        createCardButton.setOnClickListener{
            getFields()
            if (cardModel.title.isEmpty())
            {
                Toast.makeText(this, "Title of card can not be empty.", Toast.LENGTH_SHORT).show()
            }
            else {
                createCard()
            }
        }
    }

    private fun getFields() {
        val title = findViewById<EditText>(R.id.new_card_name_edit_text).text.toString()
        val description = findViewById<EditText>(R.id.new_card_description_edit_text).text.toString()
        cardModel = CardModel(0, title, description)
    }

    private fun getJson(): String {
        val json = JSONObject().put("title", cardModel.title)
                               .put("description", cardModel.description)
        return json.toString()
    }

    private fun createCard() {
        val connection = HttpPostRequestHandler().execute(
            "http://kanban-project-management-api.herokuapp.com/v1/tables/${tableId}/lists/${listId}/cards",
            getJson(),
            SessionModel.instance.email,
            SessionModel.instance.token
        )
        if (connection.get().first == 201) {
            val listsActivityIntent = Intent(this, ListsActivity::class.java)
            listsActivityIntent.putExtra("groupId", groupId)
            listsActivityIntent.putExtra("tableId", tableId)
            startActivity(listsActivityIntent)
        }
        else {
            Toast.makeText(this, "Connection error, code ${connection.get().first}", Toast.LENGTH_SHORT).show()
        }
    }
}
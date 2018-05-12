package com.romk.projectmanagmentapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.romk.projectmanagmentapp.NetworkConnection.HttpGetRequestHandler
import com.romk.projectmanagmentapp.NetworkConnection.HttpPostRequestHandler
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private var emailString = String()
    private var passwordString = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        bindButtons()
    }

    fun isPasswordCorrect() : Boolean {
        when {
            passwordString.isBlank() -> return false
            else -> return true
        }
    }

    fun isEmailCorrect() : Boolean {
        when {
            emailString.isBlank() -> return false
            !emailString.contains("@") -> return false
            else -> return true
        }
    }

    fun bindButtons() {
        val loginButton = findViewById<Button>(R.id.login_button)
        val registerButton = findViewById<Button>(R.id.register_button)

        loginButton.setOnClickListener {
            emailString = findViewById<EditText>(R.id.email_edit_text).text.toString()
            passwordString = findViewById<EditText>(R.id.password_edit_text).text.toString()

            if(!isEmailCorrect()) {
                Toast.makeText(applicationContext, "Email do not match specified restrictions", Toast.LENGTH_SHORT).show()
            }
            else if(!isPasswordCorrect()) {
                Toast.makeText(applicationContext, "Password do not match specified restrictions", Toast.LENGTH_SHORT).show()
            }
            else {
                val connector = HttpPostRequestHandler().execute("http://kanban-project-management-api.herokuapp.com/v1/sessions", getJsonString())
                if(connector.get().first != 201) {
                    Toast.makeText(applicationContext, "Something was wrong", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(applicationContext, "Logged", Toast.LENGTH_SHORT).show()

                    val user = JSONObject(connector.get().second.getJSONObject("data").getJSONObject("user"), arrayOf("email", "authentication_token"))
                    val activityIntent = Intent(this, GroupsActivity::class.java)
                    activityIntent.putExtra("authentication_token", user.getString("authentication_token"))
                    activityIntent.putExtra("email", user.getString("email"))
                    startActivity(activityIntent)
                }
            }
        }

        registerButton.setOnClickListener {
            val registrationActivityIntent = Intent(this, GroupsActivity::class.java)
            startActivity(registrationActivityIntent)
        }
    }


    private fun getJsonString(): String {
        val parameters = HashMap<String, String>()
        parameters.put("email", emailString)
        parameters.put("password", passwordString)
        return JSONObject(parameters).toString()
    }
}

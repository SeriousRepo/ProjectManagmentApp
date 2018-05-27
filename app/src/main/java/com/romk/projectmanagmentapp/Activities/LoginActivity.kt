package com.romk.projectmanagmentapp.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.romk.projectmanagmentapp.Models.SessionModel
import com.romk.projectmanagmentapp.NetworkConnection.HttpPostRequestHandler
import com.romk.projectmanagmentapp.R
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
            passwordString.length < 6 -> return false
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
            emailString = findViewById<EditText>(R.id.login_email_edit_text).text.toString()
            val passwordEditText = findViewById<EditText>(R.id.login_password_edit_text)
            passwordString = passwordEditText.text.toString()

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

                    val user = JSONObject(connector.get().second.getJSONObject("data").getJSONObject("user"), arrayOf("email", "authentication_token"))
                    val session = SessionModel.instance
                    session.email = user.getString("email")
                    session.token = user.getString("authentication_token")
                    val groupsActivityIntent = Intent(this, TablesActivity::class.java)
                    startActivity(groupsActivityIntent)
                    passwordEditText.text.clear()
                }
            }
        }

        registerButton.setOnClickListener {
            val registrationActivityIntent = Intent(this, GroupsActivity::class.java)
            startActivity(registrationActivityIntent)
        }
    }


    private fun getJsonString(): String {
        val json = JSONObject().put("email", emailString)
                               .put("password", passwordString)
        return json.toString()
    }
}

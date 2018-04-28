package com.romk.projectmanagmentapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.romk.projectmanagmentapp.Connector.Connector
import com.romk.projectmanagmentapp.JsonHelper.JsonHelper
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var emailString = String()
    private var passwordString = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindButtons()
    }

    fun isPasswordCorrect(password : String) : Boolean {
        when {
            password.isBlank() -> return false
            else -> return true
        }
    }

    fun isEmailCorrect(email : String) : Boolean {
        when {
            email.isBlank() -> return false
            !email.contains("@") -> return false
            else -> return true
        }
    }

    fun bindButtons() {
        val loginButton = findViewById<Button>(R.id.login_button)
        val registerButton = findViewById<Button>(R.id.register_button)

        loginButton.setOnClickListener {
            emailString = findViewById<EditText>(R.id.email_edit_text).text.toString()
            passwordString = findViewById<EditText>(R.id.password_edit_text).text.toString()

            if(!isPasswordCorrect(passwordString)) {
                Toast.makeText(applicationContext, "Password do not match specified restrictions", Toast.LENGTH_SHORT).show()
            }
            if(isEmailCorrect(emailString)) {
                Toast.makeText(applicationContext, "Password do not match specified restrictions", Toast.LENGTH_SHORT).show()
            }
            val connector = Connector()
            connector.execute("http://kanban-project-management-api.herokuapp.com/v1/sessions", getJsonString())
            if(connector.get() != 201) {
                Toast.makeText(applicationContext, "Something was wrong ${connector.get()}", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(applicationContext, "Logged", Toast.LENGTH_SHORT).show()
            }

            //var ActivityIntent = Intent()
        }

        registerButton.setOnClickListener {
            //ToDo open register activity
        }
    }


    private fun getJsonString(): String {
        val parameters = HashMap<String, String>()
        parameters.put("email", emailString)
        parameters.put("password", passwordString)
        return JSONObject(parameters).toString()
    }
}

package com.romk.projectmanagmentapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.romk.projectmanagmentapp.NetworkConnection.HttpPostRequestHandler
import com.romk.projectmanagmentapp.R
import org.json.JSONObject

class RegistrationActivity : AppCompatActivity() {

    private var emailString = String()
    private var passwordString = String()
    private var confirmationPasswordString = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        bindButtons()
    }

    fun bindButtons() {
        val registerButton = findViewById<Button>(R.id.register_button)
        val loginButton = findViewById<Button>(R.id.login_button)

        registerButton.setOnClickListener {
            emailString = findViewById<EditText>(R.id.email_edit_text).text.toString()
            passwordString = findViewById<EditText>(R.id.password_edit_text).text.toString()
            confirmationPasswordString = findViewById<EditText>(R.id.confirm_password_edit_text).text.toString()
            if (!isEmailCorrect()) {
                Toast.makeText(applicationContext, "Email do not match specified restrictions", Toast.LENGTH_SHORT).show()
            }
            else if (!isPasswordCorrect()) {
                Toast.makeText(applicationContext, "Password do not match specified restrictions", Toast.LENGTH_SHORT).show()
            }
            else if (!isPasswordsEquals()) {
                Toast.makeText(applicationContext, "Passwords do not equals", Toast.LENGTH_SHORT).show()
            }
            else {
                val connector = HttpPostRequestHandler().execute("http://kanban-project-management-api.herokuapp.com/v1/users", getJsonString())

                if (connector.get().first != 201) {
                    Toast.makeText(applicationContext, "Something was wrong "+connector.get().first, Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(applicationContext, "Registered", Toast.LENGTH_SHORT).show()
                }
            }
        }

        loginButton.setOnClickListener {
            val loginActivityIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginActivityIntent)
        }
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

    fun isPasswordsEquals() : Boolean {
        return passwordString == confirmationPasswordString
    }

    fun getJsonString() : String {
        val user = HashMap<String, String>()
        user.put("email", emailString)
        user.put("password", passwordString)
        user.put("password_confirmation", confirmationPasswordString)
        val jsonUser = JSONObject(user)
        val data = hashMapOf(Pair("user", jsonUser))
        return JSONObject(data).toString()
    }
}
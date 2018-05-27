package com.romk.projectmanagmentapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.romk.projectmanagmentapp.Models.SessionModel
import com.romk.projectmanagmentapp.R

class LogoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)

        SessionModel.instance.email = ""
        SessionModel.instance.token = ""

        bindButtons()
    }

    private fun bindButtons() {
        val loginButton = findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener {
            val loginActivityIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginActivityIntent)
        }
    }
}
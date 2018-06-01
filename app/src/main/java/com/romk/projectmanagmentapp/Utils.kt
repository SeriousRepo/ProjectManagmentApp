package com.romk.projectmanagmentapp

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.romk.projectmanagmentapp.Activities.LogoutActivity
import com.romk.projectmanagmentapp.Models.SessionModel
import com.romk.projectmanagmentapp.NetworkConnection.HttpDeleteRequestHandler

class Utils {
    fun deleteSession(context: Context) {
        val connection = HttpDeleteRequestHandler().execute("http://kanban-project-management-api.herokuapp.com/v1/sessions",
            SessionModel.instance.email,
            SessionModel.instance.token
        )
        if (connection.get() == 200) {
            val logoutActivityIntent = Intent(context, LogoutActivity::class.java)
            context.startActivity(logoutActivityIntent)
        }
        else {
            Toast.makeText(context, "Connection error, code ${connection.get()}", Toast.LENGTH_SHORT).show()
        }
    }
}
package com.romk.projectmanagmentapp.NetworkConnection

import android.os.AsyncTask
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL

class HttpDeleteRequestHandler : AsyncTask<String, Void, Int>() {
    var responseCode = 0

    override fun doInBackground(vararg params: String): Int {
        try {
            val url = URL(params[0])
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "DELETE"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("X-User-Email", params[1])
            connection.setRequestProperty("X-User-Token", params[2])
            connection.connect()
            Log.d("email", params[1])
            Log.d("token", params[2])
            responseCode = connection.responseCode
        }
        catch (exception: Exception) {
            System.out.println(exception.message)
        }
        return responseCode
    }
}
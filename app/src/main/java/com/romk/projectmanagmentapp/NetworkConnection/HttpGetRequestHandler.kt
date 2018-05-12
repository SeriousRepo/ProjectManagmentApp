package com.romk.projectmanagmentapp.NetworkConnection

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HttpGetRequestHandler : AsyncTask<String, Void, Pair<Int, JSONObject>>() {
    var responseCode = 0
    var responseBody = JSONObject()

    override fun doInBackground(vararg params: String): Pair<Int, JSONObject> {
        try {
            val url = URL(params[0])
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("X-User-Email", params[1])
            connection.setRequestProperty("X-User-Token", params[2])
            connection.connect()

            responseCode = connection.responseCode
            if (responseCode == 200) {
                val responseReader = BufferedReader(InputStreamReader(connection.inputStream))
                var line: String
                var stringBuilder = StringBuilder()
                do {
                    line = responseReader.readLine()
                    stringBuilder.append(line)
                } while (line.isEmpty())

                responseReader.close()
                responseBody = JSONObject(stringBuilder.toString())
            }
        }
        catch (exception: Exception) {
            System.out.println(exception.message)
        }
        return Pair(responseCode, responseBody)
    }
}
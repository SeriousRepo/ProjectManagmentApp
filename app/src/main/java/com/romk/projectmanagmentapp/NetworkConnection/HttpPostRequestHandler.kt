package com.romk.projectmanagmentapp.NetworkConnection

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class HttpPostRequestHandler : AsyncTask<String, Void, Pair<Int, JSONObject>>() {
    var responseCode = 0
    var responseBody = JSONObject()

    override fun doInBackground(vararg params: String): Pair<Int, JSONObject> {
        try {
            val url = URL(params[0])
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            if(params.size > 2) {
                connection.setRequestProperty("X-User-Email", params[2])
                connection.setRequestProperty("X-User-Token", params[3])
            }
            connection.connect()
            val requestWriter = BufferedWriter(OutputStreamWriter(connection.outputStream))
            requestWriter.write(params[1])
            requestWriter.flush()
            requestWriter.close()
            responseCode = connection.responseCode
            if (responseCode == 201 || responseCode == 200) {
                val responseReader = BufferedReader(InputStreamReader(connection.inputStream))
                var line: String
                val stringBuilder = StringBuilder()
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
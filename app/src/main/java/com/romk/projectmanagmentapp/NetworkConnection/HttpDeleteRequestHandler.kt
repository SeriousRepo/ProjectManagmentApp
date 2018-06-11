package com.romk.projectmanagmentapp.NetworkConnection

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class HttpDeleteRequestHandler : AsyncTask<String, Void, Pair<Int, JSONObject>>() {
    var responseCode = 0
    var responseBody = JSONObject()

    override fun doInBackground(vararg params: String): Pair<Int, JSONObject> {
        try {
            val url = URL(params[0])
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "DELETE"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("X-User-Email", params[1])
            connection.setRequestProperty("X-User-Token", params[2])
            if (params.size == 4) {
                val requestWriter = BufferedWriter(OutputStreamWriter(connection.outputStream))
                requestWriter.write(params[3])
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

            connection.connect()
            responseCode = connection.responseCode
        }
        catch (exception: Exception) {
            System.out.println(exception.message)
        }
        return Pair(responseCode, responseBody)
    }
}
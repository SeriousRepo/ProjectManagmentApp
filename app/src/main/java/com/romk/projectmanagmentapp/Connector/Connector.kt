package com.romk.projectmanagmentapp.Connector

import android.os.AsyncTask
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class Connector : AsyncTask<String, Void, Int>() {
    var responseCode = 0
    var responseBody = JSONObject()

    override fun doInBackground(vararg params: String): Int {
        responseCode = 500
        try {
            val url = URL(params[0])
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.connect()

            val requestWriter = BufferedWriter(OutputStreamWriter(connection.outputStream))
            requestWriter.write(params[1])
            requestWriter.flush()
            requestWriter.close()
            responseCode = connection.responseCode
            if (responseCode == 201) {
                val responseReader = BufferedReader(InputStreamReader(connection.inputStream))
                var line: String = responseReader.readLine()
                val stringBuilder = StringBuilder()
                while (line.isEmpty()) {
                    stringBuilder.append(line)
                    line = responseReader.readLine()
                }
                responseReader.close()
            }
            connection.disconnect()
        }
        catch (exception: Exception) {
            System.out.println(exception.message)
        }

        return responseCode
    }
}
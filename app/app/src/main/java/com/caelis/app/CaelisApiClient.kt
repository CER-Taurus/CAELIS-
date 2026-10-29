package com.caelis.app

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

object CaelisApiClient {

    private const val BASE_URL =
        "https://caelis-api.justloxy224.workers.dev"

    private const val PREFS_NAME = "caelis_cloud"
    private const val USER_ID_KEY = "user_id"

    private fun getUserId(context: Context): String {
        val prefs = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val existing = prefs.getString(USER_ID_KEY, null)

        if (!existing.isNullOrBlank()) {
            return existing
        }

        val created = UUID.randomUUID()
            .toString()
            .replace("-", "")

        prefs.edit()
            .putString(USER_ID_KEY, created)
            .apply()

        return created
    }

    suspend fun sendMessage(
        context: Context,
        message: String
    ): String = withContext(Dispatchers.IO) {

        val payload = JSONObject()
            .put("userId", getUserId(context))
            .put("message", message)

        val response = post("/chat", payload)

        val reply = response
            .optString("reply")
            .trim()

        if (reply.isEmpty()) {
            throw IllegalStateException(
                "Сервер вернул пустой ответ"
            )
        }

        reply
    }

    suspend fun clearCloudMemory(
        context: Context
    ) = withContext(Dispatchers.IO) {

        post(
            "/memory/clear",
            JSONObject().put(
                "userId",
                getUserId(context)
            )
        )

        Unit
    }

    private fun post(
        path: String,
        payload: JSONObject
    ): JSONObject {

        val connection =
            URL("$BASE_URL$path")
                .openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 15000
            connection.readTimeout = 30000
            connection.doOutput = true

            connection.setRequestProperty(
                "Content-Type",
                "application/json; charset=utf-8"
            )

            connection.setRequestProperty(
                "Accept",
                "application/json"
            )

            OutputStreamWriter(
                connection.outputStream,
                Charsets.UTF_8
            ).use {
                it.write(payload.toString())
            }

            val status = connection.responseCode

            val stream = if (status in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val body = stream?.use { input ->
                BufferedReader(
                    InputStreamReader(
                        input,
                        Charsets.UTF_8
                    )
                ).use {
                    it.readText()
                }
            }.orEmpty()

            val json = try {
                JSONObject(body)
            } catch (_: Exception) {
                JSONObject()
            }

            if (status !in 200..299) {
                val details = json.optString("error")
                    .ifBlank {
                        json.optString("message")
                            .ifBlank { "HTTP $status" }
                    }

                throw IllegalStateException(
                    "Ошибка CAELIS API ($status): $details"
                )
            }

            return json

        } finally {
            connection.disconnect()
        }
    }
}

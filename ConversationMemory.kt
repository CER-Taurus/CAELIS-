package com.caelis.app

import android.content.Context
import org.json.JSONArray

object ConversationMemory {

    private const val PREFS_NAME = "caelis_memory"
    private const val MESSAGES_KEY = "conversation"
    private const val MAX_MESSAGES = 200

    private fun preferences(context: Context) =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    fun load(context: Context): List<String> {
        val stored = preferences(context)
            .getString(MESSAGES_KEY, null)
            ?: return emptyList()

        return try {
            val array = JSONArray(stored)
            val messages = mutableListOf<String>()

            for (i in 0 until array.length()) {
                messages.add(array.getString(i))
            }

            messages.takeLast(MAX_MESSAGES)
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun save(
        context: Context,
        messages: List<String>
    ) {
        val array = JSONArray()

        messages.takeLast(MAX_MESSAGES).forEach {
            array.put(it)
        }

        preferences(context)
            .edit()
            .putString(MESSAGES_KEY, array.toString())
            .apply()
    }

    fun clear(context: Context) {
        preferences(context)
            .edit()
            .remove(MESSAGES_KEY)
            .apply()
    }
}

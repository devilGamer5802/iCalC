package com.hatcorp.icalc.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Create a DataStore instance at the top level
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "icalc_history")

class HistoryRepository(private val context: Context) {
    companion object {
        private val HISTORY_KEY = stringPreferencesKey("calculation_history")
    }

    val historyFlow: Flow<List<String>> = context.dataStore.data.map { preferences ->
        val jsonString = preferences[HISTORY_KEY] ?: "[]"
        Json.decodeFromString<List<String>>(jsonString)
    }

    suspend fun saveHistory(historyList: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[HISTORY_KEY] = Json.encodeToString(historyList)
        }
    }
}
// ExpenseDataStore.kt
package com.sk.expencetracker

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
val Context.dataStore by preferencesDataStore(name = "expenses")

class ExpenseDataStore(private val context: Context) {

    private val EXPENSE_KEY = stringPreferencesKey("expens_list")

    // Save list of expenses as JSON
    suspend fun saveExpenses(expenses: List<String>) {
        val jsonString = Json.encodeToString(expenses)
        context.dataStore.edit { preferences ->
            preferences[EXPENSE_KEY] = jsonString
        }
    }

    // Read list of expenses as Flow
    fun getExpenses(): Flow<List<String>> {
        return context.dataStore.data
            .map { preferences ->
                preferences[EXPENSE_KEY]?.let { json ->
                    Json.decodeFromString<List<String>>(json)
                } ?: emptyList()
            }
    }
}
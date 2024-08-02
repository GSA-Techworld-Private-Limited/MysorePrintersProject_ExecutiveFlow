package com.example.mysoreprintersproject.responses
import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class UserPreferences(context: Context) {
    private val applicationContext = context.applicationContext
    private val dataStore: DataStore<Preferences> = applicationContext.createDataStore("my_data_store")

    val authToken: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_AUTH]
        }
    val refreshToken: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_REFRESH]
        }


    val userId: Flow<String?>
        get() = dataStore.data.map {  preferences ->
            preferences[USER_ID]
        }

    suspend fun saveAuthToken(authToken: String) {
        dataStore.edit { preferences ->
            preferences[KEY_AUTH] = authToken
        }
    }



    suspend fun saveUserId(userId: String){
        dataStore.edit { preferences ->
            preferences[KEY_AUTH] = userId

        }
    }

    suspend fun getUserId(): String? {
        return userId.first()
    }
    suspend fun getAuthToken(): String? {
        return authToken.first() // Get the current value of the auth token
    }
    suspend fun saveRefreshToken(refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[KEY_REFRESH] = refreshToken
        }
    }

    suspend fun getRefreshToken(): String? {
        return refreshToken.first() // Get the current value of the auth token
    }



    companion object {
        val KEY_AUTH = preferencesKey<String>("access") // Define the key here
        val KEY_REFRESH= preferencesKey<String>("refresh")
        val USER_ID= preferencesKey<String>("id")
    }
}








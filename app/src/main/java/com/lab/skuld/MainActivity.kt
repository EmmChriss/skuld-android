package com.lab.skuld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lab.skuld.ui.App


class MainActivity : ComponentActivity() {

    //lateinit var dataStore: DataStore<Preferences>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       /* dataStore = createDataStore(
            name = "settings"
        )*/

        setContent { App() }
    }
   /* suspend fun saveMTheme(theme: String) {
        val THEME = preferencesKey<String>("theme")
        dataStore.edit { preferences ->
            preferences[THEME] = theme
        }
    }

    suspend fun getMTheme(): String {
        val THEME = preferencesKey<String>("theme")
        val preferences = dataStore.data.first()
        return preferences[THEME] ?: "Light"
    }*/


}


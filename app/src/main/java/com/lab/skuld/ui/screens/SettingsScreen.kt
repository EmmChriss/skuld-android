package com.lab.skuld.ui.screens

import android.content.ContentValues.TAG
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.lab.skuld.model.CreateEvent
import com.lab.skuld.model.Event
import com.lab.skuld.model.MaybeEvent
import com.lab.skuld.model.maybeToEvent
import com.lab.skuld.ui.UIContextViewModel
import com.lab.skuld.ui.rememberLiveArray
import com.lab.skuld.ui.theme.ClownColorPalette
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

@Composable
fun ShowSettingsScreen() {

    Column (modifier = Modifier.padding(25.dp)){
        Text("Settings", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.padding(16.dp))
        Text("Theme", style = MaterialTheme.typography.h6)
        RadioButtonsThemes()
        Spacer(modifier = Modifier.padding(16.dp))
        Text("Export data", style = MaterialTheme.typography.h6)
        ExportButton()
        Spacer(modifier = Modifier.padding(10.dp))
        ImportButton()
    }
}
/*
///////////////////////First attempt, read at own risk////////////////////////
//this stays
@Composable
fun ExportButton() {
    val context = LocalContext.current
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "export.txt")
    //val file = File(context.getExternalFilesDir("../../../../Downloads"), "saved_text.txt")
    val query = Firebase.firestore.collection("users/data/${Firebase.auth.currentUser!!.uid}")
    val documents: List<Task> = rememberLiveArray(
        MaybeTask::class.java,
        query,
        ::maybeToTask,
    )
    val resultMessage = remember { mutableStateOf("") }


    Button(onClick = {
        var isFirst = true
        for (document in documents) {
            Log.i(TAG, document.toString())
            if(isFirst) {
                file.writeText(document.toString() + "\n")
                isFirst = false
            }
            else
                file.appendText(document.toString()+ "\n")
        }

        Log.i(TAG, "Saved to ${file.absolutePath}")
        resultMessage.value = "Saved to ${file.absolutePath}"
    }) {
        Text("EXPORT")
    }
    Text(resultMessage.value)
}
*/
@Composable
fun ExportButton() {
    val context = LocalContext.current
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "export.json")
    val query = Firebase.firestore.collection("users/data/${Firebase.auth.currentUser!!.uid}")
    val documents: List<Event> = rememberLiveArray(
        MaybeEvent::class.java,
        query,
        ::maybeToEvent,
    )
    val resultMessage = remember { mutableStateOf("") }

    Button(onClick = {
        val jsonArray = JSONArray()

        for (document in documents) {
            val jsonObject = JSONObject()
            jsonObject.put("id", document.id)
            jsonObject.put("startDate", document.startDate)
            jsonObject.put("endDate", document.endDate)
            jsonObject.put("title", document.title)
            jsonObject.put("checked", document.checked)
            jsonObject.put("contents", document.contents)

            jsonArray.put(jsonObject)
        }

        file.writeText(jsonArray.toString(2))

        Log.i(TAG, "Saved to ${file.absolutePath}")
        resultMessage.value = "Saved to ${file.absolutePath}"
    }) {
        Text("EXPORT")
    }
    Text(resultMessage.value)
}

@Composable
fun ImportButton() {
    val context = LocalContext.current
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "export.json")
    val gson = Gson()
    val resultMessage = remember { mutableStateOf("") }

    Button(onClick = {
        val eventsJson = file.readText()
        val events = gson.fromJson(eventsJson, Array<Event>::class.java)
        val eventNoIDs = events.map { event ->
            CreateEvent(
                startDate = event.startDate,
                endDate = event.endDate,
                title = event.title,
                checked = event.checked,
                contents = event.contents
            )
        }

        for(event in eventNoIDs) {
            Firebase.firestore
                .collection("users/data/${Firebase.auth.currentUser!!.uid}")
                .document()
                .set(event)
                .addOnSuccessListener {
                    Log.i(TAG, "Upload Success")
                }
                .addOnFailureListener {
                    Log.w(TAG, "Upload Failed")
                }
        }

        resultMessage.value = "Imported from ${file.absolutePath}"
    }) {
        Text("IMPORT")
    }
    Text(resultMessage.value)
}


@Composable
fun RadioButtonsThemes() {
    val uiContextViewModel: UIContextViewModel = viewModel()
    val radioOptions = listOf("System theme","Light", "Dark", "Clown")

    fun onOptionSelected(text: String) {
        when (text) {
            "System theme" -> {
                uiContextViewModel.theme = "System theme"
            }
            "Light" -> {
                uiContextViewModel.theme = "Light"
            }
            "Dark" -> {
                uiContextViewModel.theme = "Dark"
            }
            "Clown" -> {
                val randomNextInt = { max: Int -> (Math.random() * max).toInt() }
                ClownColorPalette = lightColors(
                    primary = Color(-0x1000000 or randomNextInt(0xFFFFFF)),
                    primaryVariant = Color(-0x1000000 or randomNextInt(0xFFFFFF)),
                    secondary = Color(-0x1000000 or randomNextInt(0xFFFFFF)),
                    secondaryVariant = Color(-0x1000000 or randomNextInt(0xFFFFFF)),
                    background = Color.White,
                    surface = Color.White,
                    onPrimary = Color(0xFFffffff),
                    onSecondary = Color(0xFFffffff),
                    onBackground = Color.Black,
                    onSurface = Color.Black,
                )
                uiContextViewModel.theme = "Clown"
            }
        }
    }

    Column {
        radioOptions.forEach { text ->
            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (text == uiContextViewModel.theme),
                        onClick = {
                            onOptionSelected(text)
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically

            ) {
                RadioButton(
                    selected = (text == uiContextViewModel.theme),
                    onClick = { onOptionSelected(text)}
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.body1.merge(),
                    modifier = Modifier.padding(start = 0.dp)
                )
            }
        }
    }
}




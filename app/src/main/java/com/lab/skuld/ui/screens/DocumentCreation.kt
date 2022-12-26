package com.lab.skuld.ui.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


data class Template(var title: String, var mapOfEntries: Map<String, *>)

@Composable
fun TemplateListView() {
    var currentIDT = remember { mutableStateOf(0) }
    if(currentIDT.value==0) {
        Column() {
            Button(onClick = { currentIDT.value = 1 }) {
                Text(text = "Completely new")
            }

            Text(text = "OR")

            Button(onClick = { currentIDT.value = 2 }) {
                Text(text = "Based on template")
            }
        }
    }

    if(currentIDT.value ==1)      {
        exampleNewNote()
    }

    if (currentIDT.value ==2)
    {
        NewNoteFromTemplate()
    }
}
 

@Composable
fun exampleNewNote(){
    NewTextEntry()
}

@Composable
fun NewNote(){

}

@Composable
fun NewNoteFromTemplate(){
    NewTextEntry("FT")
}

@Composable
fun NewTemplateView(){
    val notesList = remember {
        mutableStateListOf<String>()
    }
    NewTextEntry("Entry Title")
    Button(
        onClick = {
            notesList.add("Entry Title")
        },
        modifier = Modifier
            .fillMaxHeight()
    ) {
        Text(text = "+")
    }

}

@Composable
fun NewEntryButton() {
    Button(onClick = { /*TODO*/ }) {
        Text(text = "+")
    }
}

@Composable
fun NewTextEntry(entryHeader: String = "New Entry"){
    var textInput by rememberSaveable { mutableStateOf("") }
    TextField(
        value = textInput,
        onValueChange = {
            textInput = it
        },
        label = { Text(entryHeader) }
    )
}

@Composable
fun NewFileEntry(){

}

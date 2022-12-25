package com.lab.skuld.ui.screens

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun ShowNewNoteScreen(){
    var textInput by rememberSaveable { mutableStateOf("Text") }
    TextField(
        value = textInput,
        onValueChange = {
            textInput = it
        },
        label = { Text("New note") }
    )
}
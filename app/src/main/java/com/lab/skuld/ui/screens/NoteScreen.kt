package com.lab.skuld.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lab.skuld.ui.Event

//data class TextData(var index: Int, var header: String, var value: String)
@Composable
fun ShowNoteScreen(documentt: Event) {
    var document = eventToDocument(documentt)
    val textElementsValues = remember { mutableStateListOf<TextData>() }

    if (document.documentContents.isNotEmpty()) {
        for (element in document.documentContents) {
            textElementsValues.add(element)
        }
    }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()) {




    }




   Column(modifier = Modifier
       .padding(16.dp)
       .verticalScroll(rememberScrollState())
       .fillMaxWidth()) {

       Text(
           text = document.header,
           modifier = Modifier
               .padding(10.dp)
               .align(Alignment.CenterHorizontally)
               .wrapContentHeight()
       )
       Spacer(modifier = Modifier.padding(10.dp))
        for (data in textElementsValues) {
            Text(
                text = data.header,
                modifier = Modifier
                    .padding(5.dp)
                    //.align(Alignment.CenterHorizontally)
                    .wrapContentHeight(),
                fontSize = 25.sp
            )
            Text(
                text = data.value,
                modifier = Modifier
                    .padding(5.dp)
                    //.align(Alignment.CenterHorizontally)
                    .wrapContentHeight(),
            )
        }
    }
}

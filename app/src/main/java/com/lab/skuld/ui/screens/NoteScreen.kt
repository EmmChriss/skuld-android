package com.lab.skuld.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lab.skuld.ui.Screen
import com.lab.skuld.ui.UiContextViewModel

//import com.lab.skuld.ui.Event

//data class TextData(var index: Int, var header: String, var value: String)
@Composable
fun ShowNoteScreen(documentt: Event) {
    val document = eventToDocument(documentt)
    val textElementsValues = remember { mutableStateListOf<TextData>() }
    val viewModel: UiContextViewModel = viewModel()

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
       .fillMaxWidth())
       {

       Text(

           text = document.header,
           fontWeight = FontWeight.Bold,
           fontSize = 35.sp,
           modifier = Modifier
               .padding(10.dp)
               .wrapContentHeight()
       )
       Spacer(modifier = Modifier.padding(10.dp))
        for (data in textElementsValues) {
            Spacer(modifier = Modifier.padding(10.dp))
            Column(modifier = Modifier
                .background(color = MaterialTheme.colors.primary, shape = RoundedCornerShape(10.dp))
                .wrapContentSize(Alignment.Center)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(10.dp)
            )
            {
                Text(
                    color = MaterialTheme.colors.onPrimary,
                    text = data.header,
                    modifier = Modifier
                        .padding(5.dp)

                        .wrapContentHeight(),
                    fontSize = 25.sp

                )
                Text(
                    color = MaterialTheme.colors.onPrimary,
                    text = data.value,
                    modifier = Modifier
                        .padding(5.dp)
                        .wrapContentHeight(),
                )
            }
        }
           Spacer(modifier = Modifier.padding(20.dp))
       Button(onClick = {

           viewModel.nav.push(Screen.NewTask(documentt))


       }) {
           Text("EDIT")

       }
    }
}

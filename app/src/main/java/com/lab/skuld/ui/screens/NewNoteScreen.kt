package com.lab.skuld.ui.screens




import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lab.skuld.ui.Navigator
import com.lab.skuld.ui.Screen

data class TextData(var index: Int, var header: String, var value: String)
@Composable
fun ShowNewNoteScreen(navigator: Navigator, document: Document){

    var newHeader by remember { mutableStateOf("") }
    var documentTitle by remember { mutableStateOf("") }
    val textElementsValues = remember { mutableStateListOf<TextData>() }

    val preList = mutableListOf<TextData>()
    if(document.documentContents.isNotEmpty() and textElementsValues.isEmpty()) {

        for(element in document.documentContents){
            preList.add(element)
        }
        textElementsValues += preList
    }





    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
        .fillMaxWidth()) {


        @Composable
        fun NewElementDialog() {
            var elementHeader by remember { mutableStateOf("") }
            var isDialogVisible by remember { mutableStateOf(false) }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    onClick = { isDialogVisible = true },
                    content = { Text("Add element") },
                )
            }
            if (isDialogVisible) {
                MyAlertDialog(
                    title = {
                        Text(
                            text = "New element header",
                            style = MaterialTheme.typography.h6,
                        )
                    },
                    content = {
                        OutlinedTextField(
                            value = elementHeader,
                            onValueChange = { elementHeader = it },
                            label = { Text("header") },
                        )
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { isDialogVisible = false },
                            content = { Text("nevermind") },
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                newHeader = elementHeader
                                textElementsValues.add(TextData(textElementsValues.size, elementHeader, ""))
                                elementHeader = ""
                                isDialogVisible = false
                            },
                            content = { Text("OK") },
                        )
                    },
                    onDismiss = {
                        isDialogVisible = false
                    },
                )
            }
        }
        @Composable
        fun TextElement(textData: TextData) {
            val index = textData.index
            val header = textData.header
            val value = textData.value
            if(index>-1) {
                Text(header, modifier = Modifier.padding(5.dp), fontSize = 20.sp)
                TextField(
                    value = value,
                    onValueChange = {
                        textElementsValues[index] = textElementsValues[index].copy(value = it)
                    },
                    label = { Text("Content") }
                )
                TextButton(
                    onClick = {
                        textElementsValues[index] = textElementsValues[index].copy(index = -1)
                    },
                    content = {
                        Text(
                            "Delete element",
                            modifier = Modifier.padding(2.dp),
                            fontSize = 10.sp
                        )
                    },
                )
            }
        }




        TextField(
            value = documentTitle,
            onValueChange = {
                documentTitle = it
            },
            label = { Text("Title") },
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.padding(10.dp))

        for (data in textElementsValues)
         {
             TextElement(textData = data)
             Spacer(modifier = Modifier.padding(5.dp))
         }



        NewElementDialog()



        Button(onClick = {
            // Navigate to a different screen when the button is clicked
            navigator.push(Screen.Task(document))
        }) {
            Text("Save")
        }
    }
}








@Composable
fun MyAlertDialog(
    title: @Composable () -> Unit,
    content: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column {
                Column(Modifier.padding(24.dp)) {
                    title.invoke()
                    Spacer(Modifier.size(16.dp))
                    content.invoke()
                }
                Spacer(Modifier.size(4.dp))
                Row(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    dismissButton.invoke()
                    confirmButton.invoke()
                }
            }
        }
    }
}




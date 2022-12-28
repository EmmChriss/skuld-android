package com.lab.skuld.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@Composable
fun ShowNewNoteScreen(){
    var newHeader by remember { mutableStateOf("") }

    var textElements by remember { mutableStateMapOf<String, String>() }

    Column(modifier = Modifier.padding(16.dp)) {
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
                                textElements = textElements + Pair(elementHeader, "")
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
        for (values in textElements){
            
        }
        NewElementDialog()
        if(newHeader != ""){
        TextElement(newHeader)
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
/*
@Composable
fun Greeting() {
    var elementHeader by remember { mutableStateOf("") }
    var testMessage by remember { mutableStateOf("") }
    var isDialogVisible by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = testMessage.ifEmpty { "Element" },
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.h5,
        )
        Button(
            onClick = { isDialogVisible = true },
            content = { Text("Add element") },
        )
    }
    if (isDialogVisible) {
        MyAlertDialog(
            title = {
                Text(
                    text = "Enter Name",
                    style = MaterialTheme.typography.h6,
                )
            },
            content = {
                OutlinedTextField(
                    value = elementHeader,
                    onValueChange = { elementHeader = it },
                    label = { Text("Name") },
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
                        testMessage = elementHeader
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
*/



@Composable
fun TextElement(label: String = "New Element") {
    var textInput by rememberSaveable { mutableStateOf("Text") }
    TextField(
        value = textInput,
        onValueChange = {
            textInput = it
        },
        label = { Text(label) }
    )
}

@Composable
fun ImageElement() {
    
}
/*
@Composable
fun NewElementSetup() {
    Column(modifier = Modifier.padding(16.dp)) {
        val textFieldCount by remember { mutableStateOf (1) }
        repeat(textFieldCount) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = step1,
                onValueChange = {
                    viewModel.onStep1Changed(it)
                },
                label = {
                    Text(text = "Step 1...")
                },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent),
                trailingIcon = {
                    Icon(
                        modifier = Modifier.padding(start=10.dp),
                        imageVector = Icons.Filled.Image,
                        tint= Color.Blue,
                        contentDescription = "Select Image"
                    )
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Button(onClick = {
            textFieldCount++
        }){
            Text("Add")
        }

    }
}

*/

/*
@Composable
fun AlertDialogSample() {
    MaterialTheme {
        Column {
            val openDialog = remember { mutableStateOf(false)  }

            Button(onClick = {
                openDialog.value = true
            }) {
                Text("Click me")
            }

            if (openDialog.value) {

                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog when the user clicks outside the dialog or on the back
                        // button. If you want to disable that functionality, simply use an empty
                        // onCloseRequest.
                        openDialog.value = false
                    },
                    title = {
                        Text(text = "Dialog Title")
                    },
                    text = {
                        Text("Here is a text ")
                    },
                    confirmButton = {
                        Button(

                            onClick = {
                                openDialog.value = false
                            }) {
                            Text("This is the Confirm Button")
                        }
                    },
                    dismissButton = {
                        Button(

                            onClick = {
                                openDialog.value = false
                            }) {
                            Text("This is the dismiss Button")
                        }
                    }
                )
            }
        }

    }
}


var openLogoutDialog by remember { mutableStateOf(false) }
if (openLogoutDialog) {
    AlertDialog(
        onDismissRequest = { openLogoutDialog = false },
        title = { Text(text = "Are you sure?") },
        text = { Text("Your local data will be cleared") },
        buttons = {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { Firebase.auth.signOut() }
                ) { Text("Yes") }
                Button(
                    onClick = { openLogoutDialog = false }
                ) { Text("No") }
            }
        }
    )
}*/

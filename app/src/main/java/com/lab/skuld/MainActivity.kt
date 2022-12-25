package com.lab.skuld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lab.skuld.ui.theme.SkuldfrontendTheme
import kotlinx.coroutines.launch



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkuldfrontendTheme {
                val scaffoldState = rememberScaffoldState()
                val scope = rememberCoroutineScope()
                val currentID = remember { mutableStateOf(0) }
                
                Scaffold(
                    scaffoldState = scaffoldState,
                    drawerContent = {
                        Button(onClick = {currentID.value = 0}, Modifier.fillMaxWidth()){Text("Tasks")}
                        Button(onClick = {currentID.value = 1}, Modifier.fillMaxWidth()){Text("New note")}
                                    Text(text = "Autoclosing drawer: SOON")},
                    topBar = {
                        val titleTasks = "Tasks"
                        val titleNewNote = "Create new note"
                        var currentTitle = ""
                        when (currentID.value){
                            0 -> currentTitle = titleTasks
                            1 -> currentTitle = titleNewNote
                            else -> currentTitle = "How??"
                        }

                        TopAppBar(
                            title = { Text(currentTitle) },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        scope.launch { scaffoldState.drawerState.open() }
                                    }
                                ) {
                                    Icon(Icons.Filled.Menu, contentDescription = "Localized description")
                                }
                            }
                        )
                    },
                    floatingActionButtonPosition = FabPosition.End,
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            text = { Text("+") },
                            onClick = { currentID.value = 1 }
                        )
                    },
                    content = {
                        if(currentID.value == 0) {
                            PlaceHolderTasks("PlaceholderTask")
                        }
                        if(currentID.value == 1) {
                            PlaceholderNewNote()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}



@Composable
fun PlaceHolderTasks(text: String = "DefaultTaskString") {
    LazyColumn {
        items(count = 100) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
            {
                Text(
                    text = "$text $it",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

        }
    }
}

@Composable
fun PlaceholderNewNote(){
    var textInput by rememberSaveable { mutableStateOf("Text") }
    TextField(
        value = textInput,
        onValueChange = {
            textInput = it
        },
        label = { Text("New note") }
    )
}


@Composable
fun DrawerButton(text: String = "DWB", destinationID: Int = 0) {
    IconButton(
        onClick = {
            
        }
    ) {
        Icon(Icons.Filled.Menu, contentDescription = "Localized description")
    }
}




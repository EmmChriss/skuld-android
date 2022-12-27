package com.lab.skuld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lab.skuld.ui.screens.ExampleDocumentList
import com.lab.skuld.ui.screens.TemplateListView
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
                        Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(onClick = {scope.launch { scaffoldState.drawerState.close() };currentID.value = 0}, Modifier.fillMaxWidth(0.7f)){Text("Tasks")}
                            Button(onClick = {scope.launch { scaffoldState.drawerState.close() };currentID.value = 1}, Modifier.fillMaxWidth(0.7f)){Text("New note")}
                        } },
                    topBar = {
                        val titleTasks = "Tasks"
                        val titleNewNote = "Create new note"

                        var currentTitle = ""
                        when (currentID.value){
                            0 -> currentTitle = titleTasks
                            1 -> currentTitle = titleNewNote
                            2 -> currentTitle = titleNewNote
                            3 -> currentTitle = titleNewNote
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
                            ExampleDocumentList()
                        }
                        if(currentID.value == 1) {
                            TemplateListView()
                        }
                       /* if(currentID.value == 2) {
                            NewNote()
                        }
                        if(currentID.value == 3) {
                            NewNoteFromTemplate ()
                        }*/
                    }
                )
            }
        }
    }
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




package com.lab.skuld.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthSettings
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lab.skuld.ui.screens.ShowLoginScreen
import com.lab.skuld.ui.screens.ShowNewNoteScreen
import com.lab.skuld.ui.screens.ShowTasksScreen
import com.lab.skuld.ui.theme.SkuldFrontendTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

// Main Composable entry-point
@Composable
fun App() {
    SkuldFrontendTheme {
        Auth {
            Navigation()
        }
    }
}

@Composable
fun Auth(content: @Composable () -> Unit) {
    var loggedIn by remember { mutableStateOf(Firebase.auth.currentUser != null) }
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            loggedIn = auth.currentUser != null
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (!loggedIn) {
        ShowLoginScreen()
    } else {
        content()
    }
}

sealed class Screen(val title: String, val content: @Composable () -> Unit, val onBack: Screen? = null ) {
    class Tasks() : Screen(
        title = "Tasks",
        content = { ShowTasksScreen() }
    )
    class NewTask() : Screen(
        title = "New Task",
        content = { ShowNewNoteScreen() },
        onBack = Tasks()
    )
}

data class Navigator (
    val push: (Screen) -> Unit,
    val pop: () -> Unit
)


@Composable
fun Navigation() {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val menuOptions = remember { listOf(
        /* All screens */
        Screen.Tasks(),
        Screen.NewTask()
    ) }
    var currentMenuOption: Screen by remember { mutableStateOf(
        /* Default screen */
        Screen.Tasks()
    ) }

    /* Utility method in navigation */
    val navigateTo: (Screen) -> Unit = remember { { screen ->
        currentMenuOption = screen

        // make sure drawer is closed
        if (scaffoldState.drawerState.isOpen)
            scope.launch { scaffoldState.drawerState.close() }
    } }

    /* Log out modal dialog */
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
    }

    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            menuOptions.forEach {
                MenuOption(it.title) { navigateTo(it) }
            }
            MenuOption("Log out") {
                openLogoutDialog = true
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(currentMenuOption.title) },
                navigationIcon = {
                    when (currentMenuOption.onBack) {
                        null -> IconButton(
                            onClick = { scope.launch {
                                scaffoldState.drawerState.open()
                            } },
                            content = { Icon(Icons.Filled.Menu, contentDescription = "Localized description") }
                        )
                        else -> {
                            IconButton(
                                onClick = { currentMenuOption = currentMenuOption.onBack!! },
                                content = { Icon(Icons.Filled.ArrowBack, contentDescription = "Localized description") }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                content = { Icon(Icons.Filled.Add, contentDescription = "Localized description") },
                onClick = { currentMenuOption = Screen.NewTask() }
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                currentMenuOption.content()
            }
        }
    )

    // make sure back presses are handled correctly
    if (openLogoutDialog) {
        BackHandler { openLogoutDialog = false }
    } else if (scaffoldState.drawerState.isOpen) {
        BackHandler { scope.launch { scaffoldState.drawerState.close() } }
    } else if (currentMenuOption.onBack != null) {
        BackHandler { navigateTo(currentMenuOption.onBack!!) }
    }
}

@Composable
fun Menu() {

}

@Composable
fun MenuOption(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, Modifier.fillMaxWidth()) { Text(text) }
}

@Composable
fun AppBar() {

}

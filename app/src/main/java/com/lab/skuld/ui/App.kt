package com.lab.skuld.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lab.skuld.ui.screens.Document
import com.lab.skuld.ui.screens.ShowLoginScreen
import com.lab.skuld.ui.screens.ShowNewNoteScreen
import com.lab.skuld.ui.screens.ShowTasksScreen
import com.lab.skuld.ui.theme.SkuldFrontendTheme
import kotlinx.coroutines.launch

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

    if (false) {
        ShowLoginScreen()
    } else {
        content()
    }

}

sealed class Screen(val title: String, val content: @Composable (navigator: Navigator) -> Unit, val onBack: Screen? = null ) {
    class Tasks() : Screen(
        title = "Tasks",
        content = { navigator -> ShowTasksScreen(navigator) }
    )
    class NewTask(document: Document) : Screen(
        title = "New Task",
        content = { ShowNewNoteScreen(document) },
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
        Screen.NewTask(document = Document("", null, documentContents = listOf()))
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
    val navigator = Navigator(
        push = { screen ->
            // Push a new screen onto the stack
            currentMenuOption = screen
        },
        pop = {
            // Pop the current screen off the stack
            currentMenuOption = currentMenuOption.onBack!!
        }
    )

    currentMenuOption.content(navigator)

    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Skuld", modifier = Modifier.padding(16.dp), fontSize = 40.sp, fontWeight = FontWeight.Bold)
                Divider(modifier = Modifier.padding(7.dp))
                menuOptions.forEach {
                    MenuOption(it.title) { navigateTo(it) }
                }
                MenuOption("Log out") {
                    openLogoutDialog = true
                }
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
                onClick = {
                    currentMenuOption = Screen.NewTask(document = Document("", null, documentContents = listOf()))
                }
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                currentMenuOption.content(navigator)
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
    Button(onClick = onClick, Modifier.fillMaxWidth(0.7f)) { Text(text) }
}

@Composable
fun AppBar() {

}

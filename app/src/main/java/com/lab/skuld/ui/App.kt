package com.lab.skuld.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lab.skuld.ui.screens.ShowNewNoteScreen
import com.lab.skuld.ui.screens.ShowTasksScreen
import com.lab.skuld.ui.theme.SkuldFrontendTheme
import kotlinx.coroutines.launch

// Main Composable entry-point
@Composable
fun App() {
    SkuldFrontendTheme {
        Navigation()
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
    val currentMenuOption: MutableState<Screen> = remember { mutableStateOf(
        /* Default screen */
        Screen.Tasks()
    ) }

    /* Utility method in navigation */
    val navigateTo: (Screen) -> Unit = remember { { screen ->
        currentMenuOption.value = screen

        // make sure drawer is closed
        if (scaffoldState.drawerState.isOpen)
            scope.launch { scaffoldState.drawerState.close() }
    } }

    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            menuOptions.forEach {
                MenuOption(it) { navigateTo(it) }
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(currentMenuOption.value.title) },
                navigationIcon = {
                    when (currentMenuOption.value.onBack) {
                        null -> IconButton(
                            onClick = { scope.launch {
                                scaffoldState.drawerState.open()
                            } },
                            content = { Icon(Icons.Filled.Menu, contentDescription = "Localized description") }
                        )
                        else -> {
                            IconButton(
                                onClick = { currentMenuOption.value = currentMenuOption.value.onBack!! },
                                content = { Icon(Icons.Filled.ArrowBack, contentDescription = "Localized description") }
                            )
                            // make sure back presses are handled correctly
                            BackHandler {
                                navigateTo(currentMenuOption.value.onBack!!)
                            }
                        }
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                content = { Icon(Icons.Filled.Add, contentDescription = "Localized description") },
                onClick = { currentMenuOption.value = Screen.NewTask() }
            )
        },
        content = { currentMenuOption.value.content }
    )
}

@Composable
fun Menu() {

}

@Composable
fun MenuOption(screen: Screen, onClick: () -> Unit) {
    Button(onClick = onClick, Modifier.fillMaxWidth()) { Text(screen.title) }
}

@Composable
fun AppBar() {

}
package com.lab.skuld.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
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
    class Custom(
        title: String,
        content: @Composable () -> Unit,
        onBack: Screen?
    ) : Screen(title, content, onBack)
    class Tasks : Screen(
        title = "Tasks",
        content = { ShowTasksScreen() }
    )
    class NewTask : Screen(
        title = "New Task",
        content = { ShowNewNoteScreen() },
        onBack = Tasks()
    )
}

data class Navigator (
    val push: (Screen) -> Unit,
    val pop: () -> Unit,
    val set: (Screen) -> Unit,
)

class UiContextViewModel : ViewModel() {
    private var _nav: Navigator? = null
    val nav
        get() = _nav!!

    fun setNav(nav: Navigator) {
        this._nav = nav
    }
}

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

    /* Create ViewModel containing navigation callbacks */
    val viewModel : UiContextViewModel = viewModel()
    viewModel.setNav(Navigator(
        push = { screen ->
            navigateTo(
                Screen.Custom(
                    title = screen.title,
                    content = screen.content,
                    onBack = currentMenuOption
                )
            )
        },
        pop = { currentMenuOption.onBack?.let { screen -> navigateTo(screen) } },
        set = { screen -> currentMenuOption = screen }
    ))

    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            menuOptions.forEach {
                MenuOption(it) { navigateTo(it) }
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
                            // make sure back presses are handled correctly
                            BackHandler {
                                navigateTo(currentMenuOption.onBack!!)
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
                onClick = { currentMenuOption = Screen.NewTask() }
            )
        },
        content = { currentMenuOption.content() }
    )
}

@Composable
fun MenuOption(screen: Screen, onClick: () -> Unit) {
    Button(onClick = onClick, Modifier.fillMaxWidth()) { Text(screen.title) }
}

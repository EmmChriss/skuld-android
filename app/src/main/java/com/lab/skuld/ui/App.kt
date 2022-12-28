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
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
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
import com.lab.skuld.ui.screens.LoadingState
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

    if (!loggedIn) {
        ShowLoginScreen()
    } else {
        content()
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

data class Navigator(
    val push: (Screen) -> Unit,
    val pop: () -> Unit,
    val set: (Screen) -> Unit,
)

class UiContextViewModel : ViewModel() {
    private var _nav: Navigator? = null
    private var _loadingBar:  MutableState<Boolean>? = null
    val nav
        get() = _nav!!

    var loadingBarEnabled by _loadingBar!!

    fun setNav(nav: Navigator) {
        this._nav = _nav ?: nav
    }

    fun setLoadingBar(loadingBar: MutableState<Boolean>) {
        this._loadingBar = _loadingBar ?: loadingBar
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

    val loadingBar = remember { mutableStateOf(false) }

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
    viewModel.setLoadingBar(loadingBar)

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
            if (loadingBar.value) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
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
fun MenuOption(title: String, onClick: () -> Unit) {
    Button(onClick = onClick, Modifier.fillMaxWidth(0.7f)) { Text(title) }
}


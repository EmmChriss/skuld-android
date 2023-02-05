package com.lab.skuld.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lab.skuld.model.Event
import com.lab.skuld.ui.screens.ShowCalendarScreen
import com.lab.skuld.ui.screens.ShowLoginScreen
import com.lab.skuld.ui.screens.ShowNewNoteScreen
import com.lab.skuld.ui.screens.ShowNoteScreen
import com.lab.skuld.ui.screens.ShowSettingsScreen
import com.lab.skuld.ui.screens.ShowTasksScreen
import com.lab.skuld.ui.screens.emptyEventOrTask
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

sealed class Screen(
    val title: String,
    val content: @Composable () -> Unit,
    val onBack: Screen? = null,
    val searchBarShown: Boolean = true
) {
    class Custom(
        title: String,
        content: @Composable () -> Unit,
        onBack: Screen?,
        searchBarShown: Boolean
    ) : Screen(title, content, onBack, searchBarShown)

    class NewTask(document: Event = emptyEventOrTask) : Screen(
        title = "New Task",
        content = { ShowNewNoteScreen(document) },
        onBack = Tasks(),
        searchBarShown = false
    )
    class TaskP(event: Event) : Screen(
        title = "Task",
        content = { ShowNoteScreen(event)},
        onBack = Tasks(),
        searchBarShown = false
    )
    class Tasks : Screen(
        title = "Tasks",
        content = { ShowTasksScreen() }
    )

    class Calendar : Screen(
        title = "Calendar",
        content = { ShowCalendarScreen() }
    )

    class Settings() : Screen(
        title = "Settings",
        content = { ShowSettingsScreen() },
        searchBarShown = false
    )
}

data class Navigator(
    /// Replace top-level Screen with given Screen, same as pop(), push(screen)
    val replace: (Screen) -> Unit,
    val push: (Screen) -> Unit,
    val pop: () -> Unit,
    val set: (Screen) -> Unit,
)

class LoadingBar {
    var enabled by mutableStateOf(false)
}

class SearchBar {
    var query by mutableStateOf(TextFieldValue())
}

class UIContextViewModel : ViewModel() {
    private var _nav: Navigator? = null
    val nav get() = _nav!!

    private var _loadingBar: LoadingBar? = null
    val loadingBar get() = _loadingBar!!

    private var _searchBar: SearchBar? = null
    val searchBar get() = _searchBar!!

    fun setNav(nav: Navigator) {
        this._nav = _nav ?: nav
    }

    fun setLoadingBar(loadingBar: LoadingBar) {
        this._loadingBar = _loadingBar ?: loadingBar
    }
    var theme: String by mutableStateOf("System theme")


    fun setSearchBar(searchBar: SearchBar) {
        this._searchBar = _searchBar ?: searchBar
    }
}

@Composable
fun Navigation() {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    // Navigation states
    val menuOptions = remember { listOf(
        /* All screens */
        Screen.Tasks(),
        Screen.Calendar(),
        Screen.NewTask(),
        Screen.Settings(),
    ) }
    var currentMenuOption: Screen by remember { mutableStateOf(
        /* Default screen */
        Screen.Tasks()
    ) }

    /* Create ViewModel containing navigation callbacks, populate it once */
    val loadingBar = remember { LoadingBar() }
    val navigateTo: (Screen) -> Unit = remember { { screen ->
        currentMenuOption = screen

        // make sure drawer is closed
        if (scaffoldState.drawerState.isOpen)
            scope.launch { scaffoldState.drawerState.close() }
    } }
    val searchBar = remember { SearchBar() }

    val viewModel: UIContextViewModel = viewModel()
    LaunchedEffect(viewModel) {
        viewModel.setNav(Navigator(
            replace = { screen ->
                navigateTo(
                    Screen.Custom(
                        title = screen.title,
                        content = screen.content,
                        onBack = currentMenuOption.onBack,
                        searchBarShown = screen.searchBarShown
                    )
                )
            },
            push = { screen ->
                navigateTo(
                    Screen.Custom(
                        title = screen.title,
                        content = screen.content,
                        onBack = currentMenuOption,
                        searchBarShown = screen.searchBarShown
                    )
                )
            },
            pop = { currentMenuOption.onBack?.let { screen -> navigateTo(screen) } },
            set = { screen -> currentMenuOption = screen }
        ))
        viewModel.setLoadingBar(loadingBar)
        viewModel.setSearchBar(searchBar)
    }

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

    /* Dispatch on back button pressed */
    val focusManager = LocalFocusManager.current
    BackHandler {
        /* Do clear focus of everything, in any case */
        focusManager.clearFocus()

        if (openLogoutDialog) {
            openLogoutDialog = false
        } else if (scaffoldState.drawerState.isOpen) {
            scope.launch { scaffoldState.drawerState.close() }
        } else if (currentMenuOption.onBack != null) {
            navigateTo(currentMenuOption.onBack!!)
        } else {
            /* TODO: exit application */
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally, ) {
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
            @Composable
            fun navigationIcon() =
                when (currentMenuOption.onBack) {
                    null -> IconButton(
                        onClick = {
                            scope.launch {
                                scaffoldState.drawerState.open()
                            }
                        },
                        content = {
                            Icon(Icons.Filled.Menu, contentDescription = "Localized description")
                        }
                    )

                    else -> {
                        IconButton(
                            onClick = { currentMenuOption = currentMenuOption.onBack!! },
                            content = {
                                Icon(
                                    Icons.Filled.ArrowBack,
                                    contentDescription = "Localized description"
                                )
                            }
                        )
                    }
                }

            @Composable
            fun loadingBar() {
                if (loadingBar.enabled) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }

            @Composable
            fun topAppBar() {
                TopAppBar(
                    title = {
                        Text(currentMenuOption.title)
                        loadingBar()
                    },
                    navigationIcon = { navigationIcon() },
                )
            }

            @Composable
            fun searchBarAppBar() {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(MaterialTheme.colors.onBackground)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        val shape = RoundedCornerShape(32.dp)
                        TextField(
                            value = searchBar.query,
                            onValueChange = { searchBar.query = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colors.background, shape),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            ),
                            singleLine = true,
                            placeholder = { Text(currentMenuOption.title) },
                            shape = shape,
                            leadingIcon = { navigationIcon() }
                        )
                    }
                }
            }

            if (currentMenuOption.searchBarShown) {
                searchBarAppBar()
            } else {
                topAppBar()
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
}

@Composable
fun MenuOption(title: String, onClick: () -> Unit) {
    Button(onClick = onClick, Modifier.fillMaxWidth(0.7f)) { Text(title) }
}


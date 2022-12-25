package com.lab.skuld.ui.screens

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase

data class LoadingState private constructor(val status: Status, val msg: String? = null) {
    companion object {
        val LOADED = LoadingState(Status.SUCCESS)
        val IDLE = LoadingState(Status.IDLE)
        val LOADING = LoadingState(Status.RUNNING)
        fun error(msg: String?) = LoadingState(Status.FAILED, msg)
    }

    enum class Status {
        RUNNING,
        SUCCESS,
        FAILED,
        IDLE,
    }
}

class LoginScreenViewModel : ViewModel() {
    val loadingState = MutableStateFlow(LoadingState.IDLE)

    fun registerWithEmailAndPassword(email: String, password: String, passwordConfirm: String) = viewModelScope.launch {
        if (password != passwordConfirm)
            loadingState.emit(LoadingState.error("Passwords do not match"))

        try {
            loadingState.emit(LoadingState.LOADING)
            Firebase.auth.createUserWithEmailAndPassword(email, password)
            loadingState.emit(LoadingState.LOADED)
        } catch (e: Exception) {
            loadingState.emit(LoadingState.error(e.localizedMessage))
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)
            Firebase.auth.signInWithEmailAndPassword(email, password).await()
            loadingState.emit(LoadingState.LOADED)
        } catch (e: Exception) {
            loadingState.emit(LoadingState.error(e.localizedMessage))
        }
    }

    fun signInWithCredential(credential: AuthCredential) = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)
            Firebase.auth.signInWithCredential(credential).await()
            loadingState.emit(LoadingState.LOADED)
        } catch (e: Exception) {
            loadingState.emit(LoadingState.error(e.localizedMessage))
        }
    }

    fun signInAnonymously() = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)
            Firebase.auth.signInAnonymously().await()
            loadingState.emit(LoadingState.LOADED)
        } catch (e: Exception) {
            loadingState.emit(LoadingState.error(e.localizedMessage))
        }
    }
}

enum class Action {
    LOGIN,
    REGISTER
}

@Composable
fun ShowLoginScreen() {
    val viewModel: LoginScreenViewModel = viewModel()

    var currentAction: Action by remember { mutableStateOf(Action.LOGIN) }
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.loadingState.collectAsState()

    Scaffold(
        scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TopAppBar(
                    elevation = 1.dp,
                    title = {
                        Text(text =
                            when(currentAction) {
                                Action.LOGIN -> "Login"
                                Action.REGISTER -> "Register"
                            }
                        )
                    }
                )
            }
            if (state.status == LoadingState.Status.RUNNING) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    when (currentAction) {
                        Action.LOGIN -> {
                            LoginForm(viewModel)
                            Button(
                                content = { Text("Don't have an account?") },
                                onClick = { currentAction = Action.REGISTER }
                            )
                        }
                        Action.REGISTER -> {
                            RegisterForm(viewModel)
                            Button(
                                content = { Text("Already have an account?") },
                                onClick = { currentAction = Action.LOGIN }
                            )
                        }
                    }

                    /* Provider separator */
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.caption,
                        text = "Login with"
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    ProviderButtons(viewModel)

                    when(state.status) {
                        LoadingState.Status.SUCCESS -> {
                            Text(text = "Success")
                        }
                        LoadingState.Status.FAILED -> {
                            Text(text = state.msg ?: "Error")
                        }
                        else -> {}
                    }
                }
            )
        }
    )
}

@Composable
private fun LoginForm(viewModel: LoginScreenViewModel) {
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = userEmail,
        label = {
            Text(text = "Email")
        },
        onValueChange = {
            userEmail = it
        }
    )

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = PasswordVisualTransformation(),
        value = userPassword,
        label = {
            Text(text = "Password")
        },
        onValueChange = {
            userPassword = it
        }
    )

    Button(
        modifier = Modifier.fillMaxWidth().height(50.dp),
        enabled = userEmail.isNotEmpty() && userPassword.isNotEmpty(),
        content = {
            Text(text = "Login")
        },
        onClick = {
            viewModel.signInWithEmailAndPassword(userEmail.trim(), userPassword.trim())
        }
    )
}

@Composable
private fun RegisterForm(viewModel: LoginScreenViewModel) {
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var userPasswordConfirm by remember { mutableStateOf("") }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = userEmail,
        label = { Text(text = "Email") },
        onValueChange = { userEmail = it }
    )

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = PasswordVisualTransformation(),
        value = userPassword,
        label = { Text(text = "Password") },
        onValueChange = { userPassword = it }
    )

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = PasswordVisualTransformation(),
        value = userPasswordConfirm,
        label = { Text(text = "Confirm passowrd") },
        onValueChange = { userPasswordConfirm = it }
    )

    Button(
        modifier = Modifier.fillMaxWidth().height(50.dp),
        enabled = userEmail.isNotEmpty() && userPassword.isNotEmpty(),
        content = { Text(text = "Register") },
        onClick = { viewModel.registerWithEmailAndPassword(userEmail.trim(), userPassword.trim(), userPasswordConfirm.trim()) }
    )
}

@Composable
private fun ProviderButtons(viewModel: LoginScreenViewModel) {
    val context = LocalContext.current
    // val token = stringResource(R.string.default_web_client_id)

    // Equivalent of onActivityResult
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            viewModel.signInWithCredential(credential)
        } catch (e: ApiException) {
            Log.w("TAG", "Google sign in failed", e)
        }
    }

    /* Google */
    OutlinedButton(
        border = ButtonDefaults.outlinedBorder.copy(width = 1.dp),
        modifier = Modifier.fillMaxWidth().height(50.dp),
        onClick = {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // .requestIdToken(token)
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            launcher.launch(googleSignInClient.signInIntent)
        },
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    // Icon(
                    //     tint = Color.Unspecified,
                    //     painter = painterResource(id = R.drawable.googleg_standard_color_18),
                    //     contentDescription = null,
                    // )
                    Text(
                        style = MaterialTheme.typography.button,
                        color = MaterialTheme.colors.onSurface,
                        text = "Google"
                    )
                    Icon(
                        tint = Color.Transparent,
                        imageVector = Icons.Default.MailOutline,
                        contentDescription = null,
                    )
                }
            )
        }
    )

    /* Anonymous */
    OutlinedButton(
        border = ButtonDefaults.outlinedBorder.copy(width = 1.dp),
        modifier = Modifier.fillMaxWidth().height(50.dp),
        onClick = { viewModel.signInAnonymously() },
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    // Icon(
                    //     tint = Color.Unspecified,
                    //     painter = painterResource(id = R.drawable.googleg_standard_color_18),
                    //     contentDescription = null,
                    // )
                    Text(
                        style = MaterialTheme.typography.button,
                        color = MaterialTheme.colors.onSurface,
                        text = "Anonymous"
                    )
                    Icon(
                        tint = Color.Transparent,
                        imageVector = Icons.Default.MailOutline,
                        contentDescription = null,
                    )
                }
            )
        }
    )
}

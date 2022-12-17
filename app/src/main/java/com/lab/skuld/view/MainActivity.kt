package com.lab.skuld.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lab.skuld.R


class MainActivity : AppCompatActivity() {



    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result: FirebaseAuthUIAuthenticationResult? ->
        Log.i("login result", result.toString())
    }

    private fun startSignIn() {
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(listOf(
                AuthUI.IdpConfig.EmailBuilder().build()
            ))
            // see
//           TODO: .setTheme(R.style.)
            .build()
        signInLauncher.launch(signInIntent)
    }

    override fun onStart() {
        super.onStart()
        val auth = Firebase.auth
        if (auth.currentUser != null) {
            startSignIn()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
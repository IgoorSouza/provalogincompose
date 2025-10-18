package com.igor.provalogincompose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import com.igor.provalogincompose.ui.theme.ProvalogincomposeTheme

class HomeActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContent {
            ProvalogincomposeTheme {
                HomeScreen(
                    auth = auth,
                    onLogout = {
                        auth.signOut()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}
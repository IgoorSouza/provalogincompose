package com.igor.provalogincompose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import com.igor.provalogincompose.ui.theme.ProvalogincomposeTheme

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        setContent {
            ProvalogincomposeTheme {
                AuthScreen(
                    auth = auth,
                    onAuthSuccess = {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

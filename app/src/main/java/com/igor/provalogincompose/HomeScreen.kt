package com.igor.provalogincompose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.igor.provalogincompose.ui.theme.ErrorColor

@Composable
fun HomeScreen(auth: FirebaseAuth, onLogout: () -> Unit) {
    val user = auth.currentUser
    val email = user?.email ?: "Usuário Desconhecido"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bem-vindo, $email",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(top = 12.dp)
        ) {
            Text("Sair", color = Color.White)
        }
    }
}
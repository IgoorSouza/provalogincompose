package com.igor.provalogincompose

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.igor.provalogincompose.ui.theme.PrimaryColor
import com.igor.provalogincompose.ui.theme.SecondaryColor

@Composable
fun AuthScreen(auth: FirebaseAuth, onAuthSuccess: () -> Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }

    val title = if (isLoginMode) "Fazer Login" else "Criar conta"
    val buttonText = if (isLoginMode) "Entrar" else "Cadastrar"
    val infoText = if (isLoginMode) "Não tem uma conta? " else "Já possui uma conta? "
    val registerText = if (isLoginMode) "Se cadastre!" else "Entre!"
    val action = if (isLoginMode) { -> login(auth, context, email, password, onAuthSuccess) }
    else { -> register(auth, context, email, password, onAuthSuccess) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .imePadding()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Senha") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
        )

        Button(
            onClick = action,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(bottom = 12.dp)
        ) {
            Text(buttonText, color = Color.White)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = infoText,
                color = Color.Black
            )
            Text(
                text = registerText,
                color = SecondaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    isLoginMode = !isLoginMode
                }
            )
        }
    }
}

private fun login(
    auth: FirebaseAuth,
    context: Context,
    email: String,
    password: String,
    onAuthSuccess: () -> Unit
) {
    if (email.isEmpty() || password.isEmpty()) {
        Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onAuthSuccess()
            } else {
                val exception = task.exception
                when (exception) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        Toast.makeText(context, "Usuário ou senha inválidos.", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(context, "Erro ao logar: ${exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
}

private fun register(
    auth: FirebaseAuth,
    context: Context,
    email: String,
    password: String,
    onAuthSuccess: () -> Unit
) {
    if (email.isEmpty() || password.isEmpty()) {
        Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                onAuthSuccess()
            } else {
                val exception = task.exception
                when (exception) {
                    is FirebaseAuthUserCollisionException -> {
                        Toast.makeText(context, "Este email já está em uso.", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(context, "Erro ao registrar: ${exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
}
package com.igor.provalogincompose

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(auth: FirebaseAuth, onAuthSuccess: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoginMode by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }

    val title = if (isLoginMode) "Bem-vindo de volta" else "Crie sua conta"
    val buttonText = if (isLoginMode) "Entrar" else "Cadastrar"
    val infoText = if (isLoginMode) "Não tem uma conta? " else "Já possui uma conta? "
    val registerText = if (isLoginMode) "Cadastre-se" else "Entrar"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF8EC5FC),
                        Color(0xFFE0C3FC)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
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
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Senha") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Senha") },
                    trailingIcon = {
                        val visibilityIcon = if (passwordVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Default.VisibilityOff

                        val description = if (passwordVisible) "Ocultar senha" else "Mostrar senha"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = visibilityIcon, contentDescription = description)
                        }
                    },
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                Button(
                    onClick = {
                        isLoading = true
                        val action = if (isLoginMode) {
                            { login(auth, email, password, onAuthSuccess) { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                                isLoading = false
                            } }
                        } else {
                            { register(auth, email, password, onAuthSuccess) { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                                isLoading = false
                            } }
                        }
                        action()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6C63FF)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(bottom = 12.dp)
                ) {
                    if (isLoading)
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    else
                        Text(buttonText, color = Color.White, fontWeight = FontWeight.Bold)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = infoText, color = Color.Black)
                    Text(
                        text = registerText,
                        color = Color(0xFF6C63FF),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { isLoginMode = !isLoginMode }
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

private fun login(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onAuthSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    if (email.isEmpty() || password.isEmpty()) {
        onError("Preencha todos os campos")
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onAuthSuccess()
            } else {
                var errorMessage = "Erro ao fazer login"

                when (task.exception) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        errorMessage = "Usuário ou senha inválidos."
                    }
                }

                onError(errorMessage)
            }
        }
}

private fun register(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onAuthSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    if (email.isEmpty() || password.isEmpty()) {
        onError("Preencha todos os campos")
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onAuthSuccess()
            } else {
                var errorMessage = "Erro ao cadastrar"

                when (task.exception) {
                    is FirebaseAuthUserCollisionException -> {
                        errorMessage = "Este email já está em uso."
                    }
                }

                onError(errorMessage)
            }
        }
}

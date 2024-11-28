package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var rut by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF216a9d),
                        Color(0xFF1ea25a),
                        Color(0xFFa4cc39)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo en la parte superior
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Crop
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(30.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Regístrate",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF2559A8),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Username input
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Nombre de usuario") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Rut input
                    OutlinedTextField(
                        value = rut,
                        onValueChange = { rut = it },
                        label = { Text("Rut") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Error message
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Register button
                    Button(
                        onClick = {
                            if (username.text.isNotEmpty() && password.text.isNotEmpty() && email.text.isNotEmpty() && rut.text.isNotEmpty()) {
                                scope.launch {
                                    isLoading = true
                                    try {
                                        val response = RetrofitInstance.api.createUser(
                                            UserRequest(
                                                rut.text,
                                                username.text,
                                                password.text,
                                                email.text
                                            )
                                        )
                                        if (response.error == null) {
                                            navController.navigate("login")
                                        } else {
                                            errorMessage = response.error
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Error al registrar el usuario: ${e.localizedMessage}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            } else {
                                errorMessage = "Todos los campos son obligatorios."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2559A8),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        enabled = !isLoading
                    ) {
                        Text(text = "Registrarse", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Login link
                    TextButton(onClick = {
                        navController.navigate("login")
                    }) {
                        Text(
                            text = "¿Ya tienes cuenta? Inicia sesión aquí",
                            color = Color(0xFF2559A8),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

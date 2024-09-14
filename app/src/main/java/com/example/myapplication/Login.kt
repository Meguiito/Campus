package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@Composable
fun LoginForm(navController: NavController) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF047cbc)) // Color de fondo actualizado
            .padding(16.dp)
    ) {
        // Barra superior con logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color(0xFFffffff)),
            contentAlignment = Alignment.CenterStart
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(105.dp)
                    .padding(start = 16.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contenido del formulario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            // Email input
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent) // Hace el fondo del TextField transparente
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password input
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mensaje de error
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Botón de login con color actualizado
            Button(
                onClick = {
                    if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
                        scope.launch {
                            isLoading = true
                            try {
                                val response = RetrofitInstance.api.verifyUser(
                                    LoginRequest(
                                        email = email.text,
                                        password = password.text
                                    )
                                )
                                if (response.error == null) {
                                    navController.navigate("mainScreen")
                                } else {
                                    errorMessage = response.error
                                }
                            } catch (e: retrofit2.HttpException) {
                                val errorBody = e.response()?.errorBody()?.string()
                                errorMessage = "Error: ${errorBody ?: "No se pudo procesar el error"}"
                            } catch (e: Exception) {
                                errorMessage = "Error al iniciar sesión: ${e.localizedMessage}"
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        errorMessage = "Todos los campos son obligatorios."
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFfcc40d) // Color actualizado
                ),
                enabled = !isLoading
            ) {
                Text("Ingresar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enlace para ir a la pantalla de registro con color actualizado
            TextButton(onClick = {
                navController.navigate("register")
            }) {
                Text(
                    text = "¿No tienes cuenta? Regístrate aquí",
                    color = Color(0xFFc4d5df) // Color del texto actualizado
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginFormPreview() {
    LoginForm(navController = rememberNavController())
}

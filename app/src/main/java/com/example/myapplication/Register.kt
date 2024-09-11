package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.network.ApiResponse
import com.example.myapplication.network.RetrofitInstance
import com.example.myapplication.network.UserRequest
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        //email
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre de usuario
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Rut de usuario
        TextField(
            value = rut,
            onValueChange = { rut = it },
            label = { Text("Rut") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        //contraseña
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
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Registrarse")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Enlace para ir a la pantalla de login
        TextButton(onClick = {
            navController.navigate("login")
        }) {
            Text("¿Ya tienes cuenta? Inicia sesión aquí")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = rememberNavController())
}

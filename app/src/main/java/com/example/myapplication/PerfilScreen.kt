package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController,isLoggedIn: Boolean, onLogout: () -> Unit, username: String, email: String, rut: String) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Lateral Drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menú",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = {
                        navController.navigate("mainScreen")
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = {
                        // Lógica para cerrar sesión
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Imagen de fondo
                Image(
                    painter = painterResource(id = R.drawable.uctinformatica), // Reemplaza con tu imagen de fondo
                    contentDescription = "Fondo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // Ajusta la imagen para que cubra toda la pantalla
                )

                // Barra superior
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.TopCenter)
                        .background(Color(0xFF33D1FF).copy(alpha = 0.7f)), // Color con transparencia
                    contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(
                        onClick = { coroutineScope.launch { drawerState.open() } },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                    }
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(105.dp)
                            .padding(start = 16.dp),
                        contentScale = ContentScale.Fit
                    )

                    Text(
                        text = "Perfil de Usuario",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center).padding(start = 8.dp) // Espacio entre el logo y el título
                    )
                }

                // Fondo entre las barras (opcional)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp, bottom = 50.dp) // Espacio para la barra superior e inferior
                ) {
                    // Contenido del perfil
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .padding(top = 0.dp), // Sin espacio extra
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        // Imagen de perfil
                        Image(
                            painter = painterResource(id = R.drawable.usuarioa), // Cambia esto por tu recurso de imagen
                            contentDescription = "Imagen de Perfil",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color(0xFF80D8FF), CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tarjeta de información del usuario
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = MaterialTheme.shapes.large,
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF80D8FF)) // Celeste claro
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Usuario: $username",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Correo: $email",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Rut: $rut",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Botón de cerrar sesión
                        Button(
                            onClick = { onLogout() },
                            modifier = Modifier.padding(horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF80D8FF)) // Celeste
                        ) {
                            Text(text = "Cerrar Sesión", color = Color.White)
                        }
                    }
                }

                // Barra inferior
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color.Gray)
                ) {
                    Text(
                        text = "Universidad catolica de temuco.",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    MyApplicationTheme {
        PerfilScreen(
            navController = rememberNavController(),
            email = "",
            username = "",
            rut = "",
            isLoggedIn = true,
            onLogout = {}
        )
    }
}

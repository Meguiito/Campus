package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController, username: String, email: String, rut: String, onLogout: () -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Lateral Drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menú Admin",
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
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = {
                        onLogout()
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = {
                        navController.navigate("perfil/$username/$email/$rut")
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // Barra superior con logo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.TopCenter)
                        .background(Color(0xFFFCC40A)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(115.dp)
                            .offset(x = (-5).dp)
                            .padding(start = 0.dp, top = 10.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                // Fondo de la sección entre las barras
                Image(
                    painter = painterResource(id = R.drawable.uctinformatica),
                    contentDescription = "Fondo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp, bottom = 50.dp),
                    contentScale = ContentScale.Crop
                )

                // Main Buttons - Opciones del administrador
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp, bottom = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CustomButton(text = "Eliminar Usuario") {
                        //navController.navigate("eliminarUsuario")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomButton(text = "Editar Canchas") {
                        //navController.navigate("editarCanchas")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomButton(text = "Editar Artículos") {
                        //navController.navigate("editarArticulos")
                    }
                }

                // Icono para abrir el Drawer
                IconButton(
                    onClick = { coroutineScope.launch { drawerState.open() } },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                }

                // Footer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color(0xCC2B2B2B)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "© 2024 Universidad Católica de Temuco",
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}

// Reusable Custom Button
@Composable
fun adminCustomButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF33D1FF)
        )
    ) {
        Text(text = text, color = Color.Black)
    }
}

@Preview(showBackground = true)
@Composable
fun AdminScreenPreview() {
    MyApplicationTheme {
        AdminScreen(navController = rememberNavController(), username = "", email = "", rut = "", onLogout = {})
    }
}

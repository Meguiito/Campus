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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, isLoggedIn: Boolean, onLogout: () -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Lateral Drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (isLoggedIn) {
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
                        label = { Text("Cerrar sesión") },
                        selected = false,
                        onClick = {
                            onLogout()
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                }
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // Header with Logo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.TopCenter)
                        .background(Color(0xFF33D1FF)),
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

                // Fondo de la sección entre las barras
                Image(
                    painter = painterResource(id = R.drawable.uctinformatica),
                    contentDescription = "Fondo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp, bottom = 50.dp),
                    contentScale = ContentScale.Crop
                )

                // Main Buttons
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp, bottom = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CustomButton(text = "Realizar Reserva") {
                        navController.navigate("calendario")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomButton(text = "Editar Reserva", onClick = { /* Navegar a otra pantalla */ })
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomButton(text = "Eliminar Reserva", onClick = { /* Navegar a otra pantalla */ })
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
                        .background(Color.Gray)
                )
            }
        }
    )
}

@Composable
fun CustomButton(text: String, onClick: () -> Unit) {
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
fun MainScreenPreview() {
    MyApplicationTheme {
        MainScreen(navController = rememberNavController(), isLoggedIn = true, onLogout = {})
    }
}

package com.example.myapplication

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController, isLoggedIn: Boolean, onLogout: () -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val currentMonth = remember {
        YearMonth.now().month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) } // Guardar el día seleccionado

    if (!isLoggedIn) {
        navController.navigate("login")
        return
    }

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
                Image(
                    painter = painterResource(id = R.drawable.uctinformatica),
                    contentDescription = "Fondo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp, bottom = 50.dp)
                        .align(Alignment.TopStart),
                    contentScale = ContentScale.FillHeight
                )

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

                IconButton(
                    onClick = { coroutineScope.launch { drawerState.open() } },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp, top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Calendario de Reservas",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(16.dp)
                    )

                    CalendarView { selectedDate ->
                        selectedDay = selectedDate
                        // Pasar el mes y el día seleccionados al navegar a la pantalla de reserva
                        navController.navigate("reserva/${currentMonth}/${selectedDay?.dayOfMonth}")

                    }
                }

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
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(onDateSelected: (LocalDate) -> Unit) {
    val currentDate = remember { LocalDate.now() }
    val currentMonth = remember { YearMonth.now() }
    val daysInMonth = remember { currentMonth.lengthOfMonth() }

    // Estado para el día seleccionado
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + currentMonth.year,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        for (week in 0..5) { // Cambiar a 5 para que cubra todas las semanas del mes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (day in 1..7) {
                    val dayOfMonth = week * 7 + day
                    if (dayOfMonth <= daysInMonth) {
                        Box(
                            modifier = Modifier
                                .size(50.dp) // Aumentar el tamaño del cuadro
                                .background(
                                    when {
                                        // Marcar el día seleccionado
                                        selectedDate == currentMonth.atDay(dayOfMonth) -> Color.Yellow
                                        dayOfMonth == currentDate.dayOfMonth -> Color.LightGray
                                        else -> Color.LightGray
                                    }, RoundedCornerShape(8.dp)
                                )
                                .padding(4.dp), // Ajustar el padding interno
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayOfMonth.toString(),
                                color = if (selectedDate == currentMonth.atDay(dayOfMonth)) Color.White else Color.Black, // Cambiar color de texto si está seleccionado
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        selectedDate = currentMonth.atDay(dayOfMonth) // Actualizar el día seleccionado
                                        onDateSelected(selectedDate!!)
                                    },
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp // Aumentar tamaño de texto
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(50.dp)) // Ajustar espacio para días vacíos
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    MyApplicationTheme {
        CalendarScreen(navController = rememberNavController(), isLoggedIn = true, onLogout = {})
    }
}
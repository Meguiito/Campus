package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .customBorder(20.dp, 8.dp, Color(0xFF33D1FF))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CustomButton(text = "Realizar Reserva")
            Spacer(modifier = Modifier.height(16.dp))
            CustomButton(text = "Editar Reserva")
            Spacer(modifier = Modifier.height(16.dp))
            CustomButton(text = "Eliminar Reserva")
        }
    }
}


fun Modifier.customBorder(vertical: Dp, horizontal: Dp, color: Color): Modifier {
    return this
        .border(BorderStroke(vertical, color), shape = RoundedCornerShape(0.dp))
        .border(BorderStroke(horizontal, color), shape = RoundedCornerShape(0.dp))
}

@Composable
fun CustomButton(text: String) {
    Button(
        onClick = { /* TODO: Implementar funcionalidad */ },
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Yellow
        )
    ) {
        Text(text = text, color = Color.Black)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MyApplicationTheme {
        MainScreen()
    }
}

package com.smb.jc_mylogin.screens.jugar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smb.jc_mylogin.navigation.Screens


@Composable
fun JuegoTerminadoScreen(
    navController: NavController,
    puntos: Int
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mostrar los puntos obtenidos
            Text(
                text = "¡Juego Terminado!",
                color = Color.White,
                fontSize = 24.sp
            )
            Text(
                text = "Puntos obtenidos: $puntos",
                color = Color.White,
                fontSize = 20.sp
            )

            // Botón para volver al home
            Button(
                onClick = {
                    navController.navigate(Screens.HomeScreen.name) {
                        popUpTo(Screens.HomeScreen.name) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text(text = "Volver al Inicio", color = Color.White)
            }
        }
    }
}

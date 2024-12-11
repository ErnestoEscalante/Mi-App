import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.smb.jc_mylogin.navigation.Screens
import com.smb.jc_mylogin.screens.jugar.Dificultad
import com.smb.jc_mylogin.screens.jugar.JuegoViewModel

@Composable
fun JuegoScreen(
    navController: NavController,
    dificultad: Dificultad,
    viewModel: JuegoViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    userEmail: String
) {
    // Iniciar el juego
    LaunchedEffect(dificultad) {
        if (viewModel.indexPreguntaActual.value == 0) {
            viewModel.startGame(dificultad)
        }
    }

    // Observación de estados
    val preguntaActual by viewModel.pregunta.observeAsState(initial = emptyList())
    val indexPreguntaActual by viewModel.indexPreguntaActual.observeAsState(initial = 0)
    val puntos by viewModel.puntos.observeAsState(initial = 0)
    val eventoJuegoFinalizado by viewModel.eventoJuegoFinalizado.observeAsState()

    // Manejo del evento de finalización
    eventoJuegoFinalizado?.getContentIfNotHandled()?.let {
        actualizarPuntosFirestore(userEmail, puntos) {
            navController.navigate("${Screens.JuegoTerminadoScreen.name}/$puntos")
        }
    }

    // UI principal
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFEAB6)
    ) {
        if (preguntaActual.isNotEmpty() && indexPreguntaActual < preguntaActual.size) {
            val pregunta = preguntaActual[indexPreguntaActual]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Encabezado con el título
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE53935))
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PREGUNTA",
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }

                // Pregunta y opciones
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "${indexPreguntaActual + 1}. ${pregunta.texto}",
                        color = Color.Black,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Botones
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val opciones = pregunta.opciones.chunked(2)
                        opciones.forEach { fila ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                fila.forEach { opcion ->
                                    Button(
                                        onClick = { viewModel.submitAnswer(opcion) },
                                        modifier = Modifier
                                            .width(140.dp)
                                            .height(50.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0))
                                    ) {
                                        Text(text = opcion, color = Color.Black, fontSize = 16.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Sección de puntos
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF4CAF50))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Puntos: $puntos",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cargando preguntas...",
                    color = Color.Black,
                    fontSize = 20.sp
                )
            }
        }
    }
}



// Función para actualizar los puntos del usuario en Firestore
fun actualizarPuntosFirestore(userEmail: String, puntos: Int, onComplete: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("users").document(userEmail)

    // Obtener el puntaje actual del usuario y actualizar
    userRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val puntosActuales = document.getLong("puntos") ?: 0
                val nuevosPuntos = puntosActuales + puntos

                userRef.update("puntos", nuevosPuntos)
                    .addOnSuccessListener {
                        onComplete()
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error al actualizar los puntos: $e")
                    }
            } else {
                Log.e("Firestore", "El usuario no existe en Firestore")
            }
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error al obtener datos del usuario: $e")
        }
}


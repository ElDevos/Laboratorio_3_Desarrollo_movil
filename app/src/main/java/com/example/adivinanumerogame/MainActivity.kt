package com.example.adivinanumerogame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.w3c.dom.Text
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NumberGuessGame()
                }
            }
        }
    }
}

@Composable
fun NumberGuessGame() {

    var targetNumber by remember { mutableStateOf(Random.nextInt(0, 101)) }
    var attemptsLeft by remember { mutableStateOf(3) }
    var timeLeft by remember { mutableStateOf(60) }
    var guessInput by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("Tienes 3 intentos. ¡Buena suerte!") }
    var gameOver by remember { mutableStateOf(false) }

    // Temporizador de 60s
    LaunchedEffect(targetNumber) {
        timeLeft = 60
        while (timeLeft > 0 && !gameOver) {
            delay(1_000)
            timeLeft--
        }
        if (!gameOver) {
            gameOver = true
            feedback = "⏰ Se acabó el tiempo. El número era $targetNumber."
        }
    }

    // Interfaz
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Juego de Adivinar",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Entre 0 y 100",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(Modifier.height(24.dp))

        // Mostrar tiempo y barra de progreso
        Text("Tiempo: $timeLeft s", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
        LinearProgressIndicator(
            progress = timeLeft / 60f,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .padding(horizontal = 32.dp)
        )
        Spacer(Modifier.height(12.dp))

        // Mostrar intentos restantes
        Text("Intentos restantes: $attemptsLeft",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(Modifier.height(24.dp))

        // Campo de texto
        OutlinedTextField(
            value = guessInput,
            onValueChange = {
                if (!gameOver) guessInput = it.filter { c -> c.isDigit() }
            },
            label = { Text("Tu número") },
            singleLine = true,
            enabled = !gameOver,
            modifier = Modifier.fillMaxWidth(0.6f)
        )
        Spacer(Modifier.height(20.dp))

        // Botón Adivinar
        Button(
            onClick = {
                val guess = guessInput.toIntOrNull()
                if (guess == null) {
                    feedback = "Ingresa un número válido."
                } else if (!gameOver) {
                    when {
                        guess == targetNumber -> {
                            feedback = "¡Correcto! Adivinaste."
                            gameOver = true
                        }
                        attemptsLeft > 1 -> {
                            attemptsLeft--
                            feedback =
                                if (guess < targetNumber) "Es mayor" else "Es menor"
                        }
                        else -> {
                            attemptsLeft = 0
                            gameOver = true
                            feedback = "Sin intentos. Era $targetNumber."
                        }
                    }
                }
                guessInput = ""
            },
            enabled = !gameOver,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(48.dp)
        ) {
            Text("Adivinar")
        }
        Spacer(Modifier.height(24.dp))

        // Retroalimentación
        Text(
            text = feedback,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(Modifier.height(32.dp))

        // Botón Reiniciar
        if (gameOver) {
            Button(
                onClick = {
                    // Reset
                    targetNumber = Random.nextInt(0, 101)
                    attemptsLeft = 3
                    timeLeft = 60
                    feedback = "Tienes 3 intentos. ¡Buena suerte!"
                    gameOver = false
                },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(44.dp)
            ) {
                Text("Reiniciar")
            }
        }
    }
}

package com.griffith.imageguessergame

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreenPage(navController: NavController, backStackEntry: NavBackStackEntry) {
    //scroll incase of tilt
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Category"
    val player1Name = backStackEntry.arguments?.getString("player1Name") ?: "Player 1"
    val player2Name = backStackEntry.arguments?.getString("player2Name") ?: "Player 2"
    val isMultiplayer = player2Name.isNotBlank()

    // Use the new function to get images
    val images = remember { getImagesForCategory(categoryName) }

    var currentImageIndex by remember { mutableStateOf(0) }
    var playerGuess by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }
    var attemptCount by remember { mutableStateOf(0) }
    var score1 by remember { mutableStateOf(0) }
    var score2 by remember { mutableStateOf(0) }
    var currentPlayer by remember { mutableStateOf(1) }
    var blurRadius by remember { mutableStateOf(10.dp) }
    var canGuess by remember { mutableStateOf(true) }

    val (currentImage, correctAnswer) = images[currentImageIndex]

    // Sensor logic to detect shake
    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val (x, y, z) = it.values
                    val shakeMagnitude = Math.sqrt((x * x + y * y + z * z).toDouble())
                    if (shakeMagnitude > 12) { // Detect a strong shake
                        if (attemptCount == 1) {
                            blurRadius = 5.dp // Reduce blur by 50%
                            feedbackMessage = "Blur reduced! Try guessing again."
                        } else if (attemptCount == 2) {
                            blurRadius = 0.dp // Remove blur completely
                            feedbackMessage = "Blur removed! Try guessing again."
                        }
                        canGuess = true // Allow guessing after shake is detected
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(Unit) {
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    fun nextImage() {
        if (currentImageIndex == images.size - 1) {
            navController.navigate("resultsPage/${score1}/${score2}/${player1Name}/${player2Name}")
        } else {
            currentImageIndex++
            playerGuess = ""
            feedbackMessage = ""
            attemptCount = 0
            blurRadius = 10.dp
            canGuess = true
            currentPlayer = if (currentPlayer == 1) 2 else 1
        }
    }

    fun checkGuess(guess: String, answer: String) {
        if (!canGuess) return // Prevent guessing if shaking is required

        when {
            guess.equals(answer, ignoreCase = true) -> {
                feedbackMessage = "Correct! 🎉"
                if (attemptCount == 0) {
                    if (currentPlayer == 1) score1 += 10 else score2 += 10
                } else if (attemptCount == 1) {
                    if (currentPlayer == 1) score1 += 5 else score2 += 5
                } else {
                    if (currentPlayer == 1) score1 += 2 else score2 += 2
                }
                nextImage()
            }
            attemptCount == 0 -> {
                attemptCount++
                feedbackMessage = "Shake your phone to reduce the blur!"
                canGuess = false
            }
            attemptCount == 1 -> {
                attemptCount++
                feedbackMessage = "Shake your phone to remove the blur!"
                canGuess = false
            }
            else -> {
                feedbackMessage = "Moving to the next image!"
                nextImage()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Guess the $categoryName") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isMultiplayer) {
                    "It's ${if (currentPlayer == 1) player1Name else player2Name}'s turn!"
                } else {
                    "$player1Name, it's your turn!"
                },
                fontSize = 20.sp
            )

            Image(
                painter = painterResource(id = currentImage),
                contentDescription = correctAnswer,
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 16.dp)
                    .blur(blurRadius)
            )

            Text(text = "Guess the $categoryName!", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = playerGuess,
                onValueChange = { playerGuess = it },
                label = { Text("Enter your guess") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = canGuess // Disable input when a shake is required
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { checkGuess(playerGuess, correctAnswer) },
                enabled = canGuess // Disable button when a shake is required
            ) {
                Text(text = "Submit Guess")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (feedbackMessage.isNotEmpty()) {
                Text(text = feedbackMessage, fontSize = 18.sp)
            }
        }
    }
}

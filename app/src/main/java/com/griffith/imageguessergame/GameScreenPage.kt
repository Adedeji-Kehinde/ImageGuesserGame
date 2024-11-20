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
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreenPage(navController: NavController, backStackEntry: NavBackStackEntry) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Category"
    val player1Name = backStackEntry.arguments?.getString("player1Name") ?: "Player 1"
    val player2Name = backStackEntry.arguments?.getString("player2Name") ?: "Player 2"
    val isMultiplayer = player2Name.isNotBlank()

    // Retrieve the slider value from the arguments
    val imageCount = backStackEntry.arguments?.getInt("imageCount") ?: 2
    val isTimerEnabled = backStackEntry.arguments?.getBoolean("isTimerEnabled") == true
    val timerDuration = backStackEntry.arguments?.getInt("timerDuration") ?: 30

    // Fetch images and limit them to the selected amount
    val images = remember { getImagesForCategory(categoryName).take(imageCount) }

    var currentImageIndex by remember { mutableIntStateOf(0) }
    var playerGuess by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }
    var attemptCount by remember { mutableIntStateOf(0) }
    var score1 by remember { mutableIntStateOf(0) }
    var score2 by remember { mutableIntStateOf(0) }
    var currentPlayer by remember { mutableIntStateOf(1) }
    var blurRadius by remember { mutableStateOf(10.dp) }
    var canGuess by remember { mutableStateOf(true) }
    var isDialogOpen by remember { mutableStateOf(false) }

    // Timer State
    var timeLeft by remember { mutableIntStateOf(timerDuration) }
    var isTimeUp by remember { mutableStateOf(false) }


    val (currentImage, correctAnswer) = images[currentImageIndex]

    // Sensor event listener for shake detection
    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val (x, y, z) = it.values
                    val shakeMagnitude = sqrt((x * x + y * y + z * z).toDouble())
                    if (shakeMagnitude > 12) {
                        if (attemptCount == 1) {
                            blurRadius = 5.dp
                            feedbackMessage = "Blur reduced! Try guessing again."
                        } else if (attemptCount == 2) {
                            blurRadius = 0.dp
                            feedbackMessage = "Blur removed! Try guessing again."
                        }
                        canGuess = true
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF4E54C8), Color(0xFF8F94FB))
    )
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
            currentPlayer = if (isMultiplayer) {
                if (currentPlayer == 1) 2 else 1
            } else {
                1 // Single player always has turn as player 1
            }
        }
    }

    // Timer Effect
    LaunchedEffect(currentImageIndex, isTimerEnabled) {
        if (isTimerEnabled) {
            timeLeft = timerDuration // Reset the timer every time the image changes
            isTimeUp = false
            while (timeLeft > 0 && !isTimeUp) {
                kotlinx.coroutines.delay(1000L)
                timeLeft--
            }
            if (timeLeft <= 0) {
                isTimeUp = true
                feedbackMessage = "Time's up! Moving to the next image."
                nextImage()
            }
        }
    }

    fun checkGuess(guess: String, answer: String) {
        if (!canGuess) return

        when {
            guess.equals(answer, ignoreCase = true) -> {
                feedbackMessage = "Correct! 🎉"
                if (attemptCount == 0) {
                    if (isMultiplayer) {
                        if (currentPlayer == 1) score1 += 10 else score2 += 10
                    } else {
                        score1 += 10
                    }
                } else if (attemptCount == 1) {
                    if (isMultiplayer) {
                        if (currentPlayer == 1) score1 += 5 else score2 += 5
                    } else {
                        score1 += 5
                    }
                } else {
                    if (isMultiplayer) {
                        if (currentPlayer == 1) score1 += 2 else score2 += 2
                    } else {
                        score1 += 2
                    }
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
                actions = {
                    IconButton(onClick = { isDialogOpen = true }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(backgroundGradient)
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundGradient)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PlayerScoreBox(playerName = player1Name, score = score1, isCurrentPlayer = !isMultiplayer || currentPlayer == 1)

                if (isMultiplayer) {
                    PlayerScoreBox(playerName = player2Name, score = score2, isCurrentPlayer = currentPlayer == 2)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Timer Display
            if (isTimerEnabled) {
                Text(text = "Time Left: $timeLeft seconds", fontSize = 18.sp, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Image(
                painter = painterResource(id = currentImage),
                contentDescription = correctAnswer,
                modifier = Modifier
                    .size(300.dp)
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
                enabled = canGuess
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { checkGuess(playerGuess, correctAnswer) },
                enabled = canGuess
            ) {
                Text(text = "Submit Guess")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (feedbackMessage.isNotEmpty()) {
                Text(text = feedbackMessage, fontSize = 18.sp)
            }

            // Show pause dialog
            if (isDialogOpen) {
                AlertDialog(
                    onDismissRequest = { isDialogOpen = false },
                    title = { Text(text = "Game Paused") },
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Restart Icon and Description
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconButton(
                                    onClick = {
                                        // Restart the game
                                        score1 = 0
                                        score2 = 0
                                        currentImageIndex = 0
                                        playerGuess = ""
                                        feedbackMessage = ""
                                        attemptCount = 0
                                        blurRadius = 10.dp
                                        canGuess = true
                                        currentPlayer = 1
                                        isDialogOpen = false
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Restart Game",
                                        tint = Color.Black
                                    )
                                }
                                Text(text = "Restart", fontSize = 12.sp)
                            }

                            // Quit Icon and Description
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconButton(
                                    onClick = {
                                        // Quit the game
                                        navController.popBackStack()
                                        isDialogOpen = false
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Quit Game",
                                        tint = Color.Black
                                    )
                                }
                                Text(text = "Quit", fontSize = 12.sp)
                            }

                            // Resume Icon and Description
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconButton(
                                    onClick = { isDialogOpen = false }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Resume Game",
                                        tint = Color.Black
                                    )
                                }
                                Text(text = "Resume", fontSize = 12.sp)
                            }
                        }
                    },
                    confirmButton = {}
                )
            }
        }
    }
}
@Composable
fun PlayerScoreBox(playerName: String, score: Int, isCurrentPlayer: Boolean) {
    val boxColor = if (isCurrentPlayer) Color.Green else Color.Red
    Column(
        modifier = Modifier
            .background(boxColor, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .width(150.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = playerName,
            fontSize = 18.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Score: $score",
            fontSize = 16.sp,
            color = Color.White
        )
    }
}

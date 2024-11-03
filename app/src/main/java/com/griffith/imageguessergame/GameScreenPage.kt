// GameScreenPage.kt
package com.griffith.imageguessergame

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreenPage(navController: NavController, backStackEntry: NavBackStackEntry) {
    val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Category"
    val player1Name = backStackEntry.arguments?.getString("player1Name") ?: "Player 1"
    val player2Name = backStackEntry.arguments?.getString("player2Name") ?: "Player 2"

    // Placeholder for images and names
    val images = remember {
        when (categoryName) {
            "Animals" -> listOf(
                R.drawable.animal_fox to "Fox",
                R.drawable.animal_flamingo to "Flamingo",
                R.drawable.animal_bear to "Bear"
            )
            "Logos" -> listOf(
                R.drawable.logo_apple to "Apple",
                R.drawable.logo_nike to "Nike",
                R.drawable.logo_adidas to "Adidas"
            )
            else -> emptyList()
        }.shuffled() // Shuffle the images list when it's first created
    }

    var currentImageIndex by remember { mutableStateOf(0) }
    var previousImageIndex by remember { mutableStateOf(-1) }
    var playerGuess by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }
    var attemptCount by remember { mutableStateOf(0) }
    var showAnswer by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }

    val (currentImage, correctAnswer) = images[currentImageIndex]

    // Function to move to the next image or navigate to Results Page
    fun nextImage() {
        if (currentImageIndex == images.size - 1) {
            // Navigate to the ResultsPage with the final score
            navController.navigate("resultsPage/$score")
        } else {
            // Move to the next image while avoiding consecutive repeats
            previousImageIndex = currentImageIndex
            do {
                currentImageIndex = (currentImageIndex + 1) % images.size
            } while (currentImageIndex == previousImageIndex)

            playerGuess = ""
            feedbackMessage = ""
            attemptCount = 0
            showAnswer = false
        }
    }

    fun checkGuess(guess: String, answer: String) {
        when {
            guess.equals(answer, ignoreCase = true) -> {
                feedbackMessage = "Correct! 🎉"
                score++ // Increase score for correct answer
                nextImage()
            }
            attemptCount < 1 -> {
                attemptCount++
                feedbackMessage = "Try again!"
            }
            else -> {
                feedbackMessage = "Wrong! The answer was $answer."
                showAnswer = true
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
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showAnswer) {
                Text(text = "Answer: $correctAnswer", fontSize = 24.sp)
                LaunchedEffect(Unit) {
                    delay(3000)
                    nextImage()
                }
            } else {
                Image(
                    painter = painterResource(id = currentImage),
                    contentDescription = correctAnswer,
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 16.dp)
                )

                Text(text = "$player1Name & $player2Name, guess the $categoryName!", fontSize = 18.sp)

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = playerGuess,
                    onValueChange = { playerGuess = it },
                    label = { Text("Enter your guess") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()

                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    checkGuess(playerGuess, correctAnswer)
                }) {
                    Text(text = "Submit Guess")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (attemptCount > 0) {
                    Text(text = feedbackMessage, fontSize = 18.sp)
                }
            }
        }
    }
}

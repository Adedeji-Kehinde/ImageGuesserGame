// ResultsPage.kt
package com.griffith.imageguessergame

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsPage(navController: NavController, backStackEntry: NavBackStackEntry) {
    //scroll incase of tilt
    val scrollState = rememberScrollState()
    val playerScore1 = backStackEntry.arguments?.getString("playerScore")?.toInt() ?: 0
    val playerScore2 = backStackEntry.arguments?.getString("playerScore2")?.toInt() ?: 0
    val player1Name = backStackEntry.arguments?.getString("player1Name") ?: "Player 1"
    val player2Name = backStackEntry.arguments?.getString("player2Name") ?: "Player 2"
    val isMultiplayer = player2Name.isNotBlank()

    // Determine the winner or if it's a tie
    val winnerMessage = if (isMultiplayer) {
        when {
            playerScore1 > playerScore2 -> "$player1Name Wins!"
            playerScore2 > playerScore1 -> "$player2Name Wins!"
            else -> "It's a Tie!"
        }
    } else {
        "$player1Name Score: $playerScore1" // Only show the score for single-player
    }

    // Create a context for the share intent
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Game Results") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Results", fontSize = 24.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "$player1Name Score: $playerScore1", fontSize = 20.sp)
            if (isMultiplayer) {
                Text(text = "$player2Name Score: $playerScore2", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display the winner message
            if (isMultiplayer) {
                Text(text = winnerMessage, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))
            //Play again button
            Button(onClick = { navController.navigate("HomePage") }) {
                Text(text = "Play Again")
            }

            Spacer(modifier = Modifier.height(16.dp))

            //share result button
            Button(onClick = {
                // Create the share intent
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        if (isMultiplayer) {
                            "$winnerMessage\n$player1Name Score: $playerScore1\n$player2Name Score: $playerScore2\nCan you beat us?"
                        } else {
                            "$player1Name scored $playerScore1 points in Image Guesser Game! Can you beat this?"
                        }
                    )
                    type = "text/plain"
                }
                // Start the share activity
                context.startActivity(Intent.createChooser(shareIntent, "Share your game results!"))
            }) {
                Text(text = "Share Results")
            }
        }
    }
}

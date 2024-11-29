package com.griffith.imageguessergame

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsPage(navController: NavController, backStackEntry: NavBackStackEntry) {
    // Scroll for better experience on smaller screens
    val scrollState = rememberScrollState()
    val playerScore1 = backStackEntry.arguments?.getString("playerScore")?.toInt() ?: 0
    val playerScore2 = backStackEntry.arguments?.getString("playerScore2")?.toInt() ?: 0
    val player1Name = backStackEntry.arguments?.getString("player1Name") ?: "Player 1"
    val player2Name = backStackEntry.arguments?.getString("player2Name") ?: ""
    val isMultiplayer = player2Name.isNotBlank()

    // Database manager instance
    val context = LocalContext.current
    val databaseManager = DatabaseManager(context)

    if (isMultiplayer) {
        // Multiplayer: Update scores and results for both players
        val resultPlayer1 = when {
            playerScore1 > playerScore2 -> "win"
            playerScore1 < playerScore2 -> "loss"
            else -> "tie"
        }

        val resultPlayer2 = when {
            playerScore2 > playerScore1 -> "win"
            playerScore2 < playerScore1 -> "loss"
            else -> "tie"
        }

        // Update both players' scores and stats in the database
        LaunchedEffect(player1Name, playerScore1) {
            databaseManager.insertOrUpdatePlayerScore(player1Name, playerScore1, resultPlayer1)
        }

        LaunchedEffect(player2Name, playerScore2) {
            databaseManager.insertOrUpdatePlayerScore(player2Name, playerScore2, resultPlayer2)
        }
    } else {
        // Single Player: Only update the player's total points and games played
        LaunchedEffect(player1Name, playerScore1) {
            databaseManager.insertOrUpdatePlayerScore(player1Name, playerScore1, "null")
        }
    }

    // Determine the winner or result message
    val winnerMessage = if (isMultiplayer) {
        when {
            playerScore1 > playerScore2 -> "$player1Name Wins!"
            playerScore2 > playerScore1 -> "$player2Name Wins!"
            else -> "It's a Tie!"
        }
    } else {
        "$player1Name scored $playerScore1 points!"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Game Results", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Results",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6200EE)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Player scores
            Text(text = "$player1Name: $playerScore1 points", fontSize = 20.sp)
            if (isMultiplayer) {
                Text(text = "$player2Name: $playerScore2 points", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Winner or result message
            Text(
                text = winnerMessage,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (winnerMessage.contains("Wins")) Color.Green else Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // "Play Again" button
            Button(
                onClick = {
                    navController.navigate("gameCategory/${player1Name}/${player2Name}/${isMultiplayer}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5))
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "Play Again", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Play Again", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "Share Results" button
            Button(
                onClick = {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            if (isMultiplayer) {
                                "$winnerMessage\n$player1Name: $playerScore1 points\n$player2Name: $playerScore2 points\nCan you beat us?"
                            } else {
                                "$player1Name scored $playerScore1 points in Image Guesser Game! Can you beat this?"
                            }
                        )
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share your game results!"))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Icon(Icons.Filled.Share, contentDescription = "Share Results", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Share Results", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "See Leaderboard" button
            Button(
                onClick = { navController.navigate("Leaderboard") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
            ) {
                Icon(Icons.Filled.Star, contentDescription = "See Leaderboard", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "See Leaderboard", color = Color.White)
            }
        }
    }
}

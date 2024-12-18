package com.griffith.imageguessergame

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
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
    val backgroundGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF4E54C8), Color(0xFF8F94FB))
    )
    // Scroll for better experience on smaller screens
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Extract data from back stack arguments
    val playerScore1 = backStackEntry.arguments?.getString("playerScore")?.toInt() ?: 0
    val playerScore2 = backStackEntry.arguments?.getString("playerScore2")?.toInt() ?: 0
    val player1Name = backStackEntry.arguments?.getString("player1Name") ?: "Player 1"
    val player2Name = backStackEntry.arguments?.getString("player2Name") ?: ""
    val isMultiplayer = player2Name.isNotBlank()

    // Database manager instance
    val databaseManager = DatabaseManager(context)

    // Determine result messages for both players (Multiplayer) or single player
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

    // Insert or update player scores and stats in the database
    LaunchedEffect(player1Name, playerScore1, player2Name, playerScore2) {
        if (isMultiplayer) {
            databaseManager.insertOrUpdatePlayerScore(player1Name, playerScore1, resultPlayer1)
            databaseManager.insertOrUpdatePlayerScore(player2Name, playerScore2, resultPlayer2)
        } else {
            databaseManager.insertOrUpdatePlayerScore(player1Name, playerScore1, "null")
        }
    }

    // Determine the winner or result message
    val winnerMessage = when {
        isMultiplayer && playerScore1 > playerScore2 -> "$player1Name Wins!"
        isMultiplayer && playerScore2 > playerScore1 -> "$player2Name Wins!"
        isMultiplayer -> "It's a Tie!"
        else -> "$player1Name scored $playerScore1 points!"
    }

    // Common button style to reduce duplication
    val buttonModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)

    // Content
    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text(text ="Results",fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent // Transparent background
                ),
                modifier = Modifier.background(backgroundGradient) // Apply the gradient
            )
        },
        containerColor = Color.Transparent // Transparent scaffold background
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .background(backgroundGradient) // Light background color for the main content
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            //Display scores before winner message only for multiplayer
            if (isMultiplayer) {
                Text(text = "$player1Name: $playerScore1 points", fontSize = 20.sp, color = Color.Black)
                Text(text = "$player2Name: $playerScore2 points", fontSize = 20.sp, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Winner or result message
            Text(
                text = winnerMessage,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (winnerMessage.contains("Wins")) Color.Green else Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Buttons section with a nicer layout
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // "Play Again" button
                Button(
                    onClick = {
                        navController.navigate("gameCategory/${player1Name}/${player2Name}/${isMultiplayer}")
                    },
                    modifier = buttonModifier,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A6DBF))
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Play Again", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Play Again", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // "Share Results" button
                Button(
                    onClick = {
                        val shareText = if (isMultiplayer) {
                            "$winnerMessage\n$player1Name: $playerScore1 points\n$player2Name: $playerScore2 points\nCan you beat us?"
                        } else {
                            "$player1Name scored $playerScore1 points in Image Guesser Game! Can you beat this?"
                        }
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share your game results!"))
                    },
                    modifier = buttonModifier,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A6DBF))
                ) {
                    Icon(Icons.Filled.Share, contentDescription = "Share Results", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Share Results", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // "See Leaderboard" button
                Button(
                    onClick = { navController.navigate("Leaderboard") },
                    modifier = buttonModifier,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A6DBF))
                ) {
                    Icon(Icons.Filled.Star, contentDescription = "See Leaderboard", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "See Leaderboard", color = Color.White)
                }
            }
        }
    }
}
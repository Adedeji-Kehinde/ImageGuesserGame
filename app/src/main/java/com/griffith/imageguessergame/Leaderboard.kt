//  Leaderboard.kt
package com.griffith.imageguessergame

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class PlayerScore(val name: String, var points: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  Leaderboard(navController: NavController) {
    // Sample leaderboard data (in a real app, you might load this from a database or shared preferences)
    val leaderboard = remember { mutableStateListOf<PlayerScore>() }

    // Function to update the leaderboard with new points
    fun updateLeaderboard(playerName: String, points: Int) {
        val existingPlayer = leaderboard.find { it.name.equals(playerName, ignoreCase = true) }
        if (existingPlayer != null) {
            existingPlayer.points += points
        } else {
            leaderboard.add(PlayerScore(playerName, points))
        }
    }

    // Mock: Adding some sample data (replace this with your actual game data logic)
    LaunchedEffect(Unit) {
        updateLeaderboard("Player 1", 100)
        updateLeaderboard("Player 2", 50)
        updateLeaderboard("Player 1", 20) // Adding more points to "Player 1"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Leaderboard") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Leaderboard", fontSize = 24.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // Display the leaderboard
            LazyColumn {
                items(leaderboard) { playerScore ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = playerScore.name, fontSize = 18.sp)
                        Text(text = "${playerScore.points} pts", fontSize = 18.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { navController.navigate("HomePage") }) {
                Text(text = "Back to Home")
            }
        }
    }
}

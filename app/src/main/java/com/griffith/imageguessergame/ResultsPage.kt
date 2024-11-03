package com.griffith.imageguessergame

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsPage(navController: NavController, backStackEntry: NavBackStackEntry) {
    val player1Name = backStackEntry.arguments?.getString("player1Name") ?: "Player 1"
    val player2Name = backStackEntry.arguments?.getString("player2Name") ?: "Player 2"
    val playerScore = backStackEntry.arguments?.getString("playerScore")?.toIntOrNull() ?: 0
    val isMultiplayer = backStackEntry.arguments?.getBoolean("isMultiplayer") ?: false


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Game Over!", fontSize = 30.sp, modifier = Modifier.padding(bottom = 16.dp))
        Text(text = "Your score: $playerScore", fontSize = 24.sp, modifier = Modifier.padding(bottom = 24.dp))

        Button(
            onClick = { navController.navigate("gameCategory/${player1Name}/${player2Name}/${isMultiplayer}") },
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text(text = "Back to Categories")
        }
    }
}

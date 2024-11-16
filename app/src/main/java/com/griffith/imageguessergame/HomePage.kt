package com.griffith.imageguessergame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomePage(navController: NavController) {
    // Background gradient for a playful look
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF4E54C8), Color(0xFF8F94FB))
    )

    // State to manage the display of the "How to Play" dialog
    var showDialog by remember { mutableStateOf(false) }

    // Show the dialog when showDialog is true
    if (showDialog) {
        HowToPlayDialog(onDismiss = { showDialog = false })
    }

    Column(
        modifier = Modifier
            .background(backgroundGradient) // Set background to gradient
            .fillMaxSize() // Fill the entire screen
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular Game Logo Image
        Box(
            modifier = Modifier
                .size(120.dp) // Size of the circular image container
                .clip(CircleShape) // Clip the image to a circle
                .background(Color.LightGray) // Optional background color
        ) {
            Image(
                painter = painterResource(id = R.drawable.game_logo), // Replace with your game logo resource
                contentDescription = "Game Logo",
                contentScale = ContentScale.Crop, // Crop the image to fill the circle
                modifier = Modifier.fillMaxSize() // Fill the circular container
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Start Game Card
        Card(
            onClick = { navController.navigate("selectPlayers") },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f) // Slightly narrower for a centered look
                .aspectRatio(16 / 7f) // Custom aspect ratio for a rectangle
                .padding(8.dp)
                .border(2.dp, Color.White, RoundedCornerShape(24.dp)) // White border
        ) {
            Image(
                painter = painterResource(id = R.drawable.start_game), // Start Game image
                contentDescription = "Start Game",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Leaderboard Card
        Card(
            onClick = { navController.navigate("leaderboard") },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f) // Nearly full width
                .aspectRatio(1f) // Square shape
                .padding(8.dp)
                .border(2.dp, Color.White, RoundedCornerShape(24.dp)) // White border
        ) {
            Image(
                painter = painterResource(id = R.drawable.leaderboard), // Leaderboard image
                contentDescription = "Leaderboard",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // How to Play - Floating Action Button
        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = Color.White,
            contentColor = Color(0xFFF7418C) // Match the color theme
        ) {
            Icon(
                imageVector = Icons.Filled.Info, // Help icon
                contentDescription = "How to Play"
            )
        }
    }
}

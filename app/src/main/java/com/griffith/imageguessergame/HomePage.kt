package com.griffith.imageguessergame

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
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
    // Create a scrollable column to ensure smooth navigation
    val scrollState = rememberScrollState()

    // Background gradient for a playful look
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFBAB66), Color(0xFFF7418C)) // Warm, vibrant gradient
    )

    Column(
        modifier = Modifier
            .background(backgroundGradient) // Set background to gradient
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular Game Logo Image with Cropping
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

        // Start Game Card (Medium Rectangle with playful design)
        Card(
            onClick = { navController.navigate("selectPlayers") },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f) // Slightly narrower for a centered look
                .aspectRatio(16 / 7f) // Custom aspect ratio for an engaging shape
                .padding(8.dp)
                .border(2.dp, Color.White, RoundedCornerShape(24.dp)) // White border for emphasis
        ) {
            Image(
                painter = painterResource(id = R.drawable.start_game), // Start Game image
                contentDescription = "Start Game",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Leaderboard Card (Large Square with a vibrant design)
        Card(
            onClick = { navController.navigate("leaderboard") },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f) // Almost full width for emphasis
                .aspectRatio(1f) // Square aspect ratio
                .padding(8.dp)
                .border(2.dp, Color.White, RoundedCornerShape(24.dp)) // White border for emphasis
        ) {
            Image(
                painter = painterResource(id = R.drawable.leaderboard), // Leaderboard image
                contentDescription = "Leaderboard",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))


        // How to Play Tooltip/Icon
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Color.White, CircleShape)
                .border(2.dp, Color(0xFFF7418C), CircleShape)
                .clickable { navController.navigate("howToPlay") } // Navigate to How to Play screen
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Info, // Using the default Help icon
                contentDescription = "How to Play",
                modifier = Modifier.size(32.dp),
                tint = Color(0xFFF7418C) // Color to match the design theme
            )
        }
    }
}

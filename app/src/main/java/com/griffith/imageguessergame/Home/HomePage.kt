package com.griffith.imageguessergame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomePage(navController: NavController) {
    // Create a background with a gradient that transitions from one color to another
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF4E54C8), Color(0xFF8F94FB))
    )

    // A variable to decide whether the "How to Play" popup should show
    var showDialog by remember { mutableStateOf(false) }

    // If the "How to Play" popup should show, display it
    if (showDialog) {
        HowToPlayDialog(onDismiss = { showDialog = false })
    }

    // This is the main column for the home page
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Center items horizontally
    ) {
        GameLogo() // Add the circular game logo at the top
        Spacer(modifier = Modifier.height(30.dp))

        PlayerModeCard(
            text = "Start Game",
            onClick = { navController.navigate("selectPlayers") }
        )
        Spacer(modifier = Modifier.height(24.dp))
        PlayerModeCard(
            text = " Current Leaderboard",
            onClick = { navController.navigate("leaderboard") }
        )

        Spacer(modifier = Modifier.height(24.dp))
        HowToPlayButton(onClick = { showDialog = true }) // Add the floating "How to Play" button
    }
}

// This function shows the circular logo at the top of the home page
@Composable
fun GameLogo() {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
    ) {
        Image(
            painter = painterResource(id = R.drawable.game_logo), // Use the game logo image
            contentDescription = "Game Logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun PlayerModeCard(text: String, onClick: () -> Unit) {
    Card(
        onClick = onClick, // Handle card click
        shape = RoundedCornerShape(16.dp), // Rounded corners for the card
        modifier = Modifier
            .fillMaxWidth(0.85f) // Card width is 85% of the screen
            .aspectRatio(16 / 6f) // Set width-to-height ratio
            .shadow(8.dp, RoundedCornerShape(16.dp)), // Add a shadow effect
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6A6DBF) // Set a dark background for better contrast
        )
    ) {
        Box(
            contentAlignment = Alignment.Center, // Center content in the box
            modifier = Modifier.fillMaxSize() // Make the box fill the card
        ) {
            Text(
                text = text, // Set the text ("Single Player" or "Multiplayer")
                fontSize = 20.sp, // Set font size
                fontWeight = FontWeight.Bold, // Make text bold
                color = Color.White // White text for better readability
            )
        }
    }
}

// This is the floating button that opens the "How to Play" popup
@Composable
fun HowToPlayButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick, // What happens when the button is clicked
        containerColor = Color.White, // The button's background color
        contentColor = Color.Black // The button's icon color
    ) {
        Icon(
            imageVector = Icons.Filled.Info, // The "info" icon for the button
            contentDescription = "How to Play" // Description for accessibility tools
        )
    }
}

// This is a preview so you can see what the home page looks like while designing
@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    // Provide a mock navigation controller for testing
    HomePage(navController = NavController(LocalContext.current))
}

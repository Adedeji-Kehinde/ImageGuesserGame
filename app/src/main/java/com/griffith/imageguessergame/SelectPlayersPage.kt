package com.griffith.imageguessergame

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectPlayersPage(navController: NavController) {
    // Define a gradient background transitioning between two colors
    val backgroundGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF4E54C8), Color(0xFF8F94FB))
    )

    Scaffold(
        // Top app bar with a back button
        topBar = {
            TopAppBar(
                title = {}, // No title in the app bar
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White // Make the icon white
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent // Transparent background
                ),
                modifier = Modifier.background(backgroundGradient) // Apply the gradient
            )
        },
        containerColor = Color.Transparent // Transparent scaffold background
    ) { innerPadding ->
        Column(
            // Full-screen column with gradient and padding
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center, // Center content vertically
            horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally
        ) {
            // Display a circular logo at the top
            GameLogo(imageResource = R.drawable.select_players, contentDescription = "Game Logo")

            Spacer(modifier = Modifier.height(16.dp)) // Add space below the logo

            Text(
                text = "Select Players Mode:",
                fontSize = 26.sp, // Set font size
                fontWeight = FontWeight.Bold, // Make text bold
                textAlign = TextAlign.Center, // Center-align text
                color = Color.White // Make text white
            )

            Spacer(modifier = Modifier.height(24.dp)) // Add space below the title

            // Single Player card
            PlayerModeCard(
                text = "Single Player",
                onClick = { navController.navigate("playerDetails/false") }
            )

            Spacer(modifier = Modifier.height(24.dp)) // Add space between the cards

            // Multiplayer card
            PlayerModeCard(
                text = "Multiplayer",
                onClick = { navController.navigate("playerDetails/true") }
            )
        }
    }
}

// Reusable composable for the circular logo
@Composable
fun GameLogo(imageResource: Int, contentDescription: String) {
    Box(
        modifier = Modifier
            .size(100.dp) // Set size of the circle
            .background(Color.White, shape = CircleShape) // White circular background
    ) {
        Image(
            painter = painterResource(id = imageResource), // Load the image resource
            contentDescription = contentDescription, // Accessibility description
            contentScale = ContentScale.Crop, // Crop the image to fit
            modifier = Modifier.fillMaxSize() // Make the image fill the circle
        )
    }
}



package com.griffith.imageguessergame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCategoryPage(navController: NavController, backStackEntry: NavBackStackEntry) {
    val scrollState = rememberScrollState()
    val player1Name = backStackEntry.arguments?.getString("player1Name") ?: "Player 1"
    val player2Name = backStackEntry.arguments?.getString("player2Name") ?: ""
    val isMultiplayer = backStackEntry.arguments?.getBoolean("isMultiplayer") ?: false

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF4E54C8), Color(0xFF8F94FB))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(backgroundGradient)
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Player Info
            if (isMultiplayer) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PlayerBox(playerName = player1Name, backgroundColor = Color(0xFF4E54C8))
                    PlayerBox(playerName = player2Name, backgroundColor = Color(0xFF4E54C8))
                }
            } else {
                PlayerBox(playerName = player1Name, backgroundColor = Color(0xFF4E54C8))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // "Choose a category" Text
            Text(
                text = "Choose a category",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Categories in separate rows with Play button below
            CategoryBoxWithButton(navController, "Animals", R.drawable.animals_image, player1Name, player2Name)
            Spacer(modifier = Modifier.height(16.dp))
            CategoryBoxWithButton(navController, "Logos", R.drawable.logos_image, player1Name, player2Name)
            Spacer(modifier = Modifier.height(16.dp))
            CategoryBoxWithButton(navController, "Fruits", R.drawable.fruits_image, player1Name, player2Name)
            Spacer(modifier = Modifier.height(16.dp))
            CategoryBoxWithButton(navController, "Random", R.drawable.random_image, player1Name, player2Name)
        }
    }
}

@Composable
fun PlayerBox(playerName: String, backgroundColor: Color) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(backgroundColor, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = playerName,
            fontSize = 18.sp,
            color = Color.White,
            style = TextStyle(fontWeight = FontWeight.Bold) // Custom text style
        )
    }
}

@Composable
fun CategoryBoxWithButton(navController: NavController, categoryName: String, imageRes: Int, player1Name: String, player2Name: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(10.dp, RoundedCornerShape(16.dp)) // Shadow for depth
            .background(Color.White, shape = RoundedCornerShape(16.dp)) // White background for the whole box
            .clickable {
                // Handle navigation with category and player details
                val route = if (player2Name == null) {
                    "gameScreen/$categoryName/$player1Name"
                } else {
                    "gameScreen/$categoryName/$player1Name/$player2Name"
                }
                navController.navigate(route)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.Gray, shape = RoundedCornerShape(16.dp)) // Background with rounded edges
                .clip(RoundedCornerShape(16.dp)), // Ensures the image fits the rounded corners
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = categoryName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize() // Make image fill the whole box
            )
            // Text overlay on top of the image
            Text(
                text = categoryName,
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Play Button below the category box
        Button(
            onClick = {
                // Navigate to the game screen with selected category and player names
                val route = if (player2Name == null) {
                    "gameScreen/$categoryName/$player1Name"
                } else {
                    "gameScreen/$categoryName/$player1Name/$player2Name"
                }
                navController.navigate(route)
            },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp)
        ) {
            Text(text = "Play Now", fontSize = 18.sp)
        }
    }
}


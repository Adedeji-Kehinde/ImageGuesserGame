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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCategoryPage(navController: NavController, backStackEntry: NavBackStackEntry) {
    // Extract player and game data from navigation arguments
    val player1Name = backStackEntry.arguments?.getString("player1Name") ?: "Player 1"
    val player2Name = backStackEntry.arguments?.getString("player2Name") ?: ""
    val isMultiplayer = backStackEntry.arguments?.getBoolean("isMultiplayer") ?: false

    // UI states for settings menu
    var isMenuExpanded by remember { mutableStateOf(false) }
    var imageCount by remember { mutableStateOf(2) } // Default number of images
    var isTimerEnabled by remember { mutableStateOf(false) }
    var timerDuration by remember { mutableStateOf(30) }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF4E54C8), Color(0xFF8F94FB))
    )

    Scaffold(
        topBar = {
            // AppBar with back button and settings menu
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("selectPlayers") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { isMenuExpanded = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                    SettingsMenu(
                        expanded = isMenuExpanded,
                        onDismiss = { isMenuExpanded = false },
                        imageCount = imageCount,
                        onImageCountChange = { imageCount = it },
                        isTimerEnabled = isTimerEnabled,
                        onTimerToggle = { isTimerEnabled = !isTimerEnabled },
                        timerDuration = timerDuration,
                        onTimerDurationChange = { timerDuration = it }
                    )
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display player names
            if (isMultiplayer) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PlayerBox(playerName = player1Name)
                    PlayerBox(playerName = player2Name)
                }
            } else {
                PlayerBox(playerName = player1Name)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Choose a category",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Display category cards
            CategoryCard(navController, "Animals", R.drawable.animals_image, player1Name, player2Name, imageCount, isTimerEnabled, timerDuration)
            Spacer(modifier = Modifier.height(16.dp))
            CategoryCard(navController, "Logos", R.drawable.logos_image, player1Name, player2Name, imageCount, isTimerEnabled, timerDuration)
            Spacer(modifier = Modifier.height(16.dp))
            CategoryCard(navController, "Fruits", R.drawable.fruits_image, player1Name, player2Name, imageCount, isTimerEnabled, timerDuration)
            Spacer(modifier = Modifier.height(16.dp))
            CategoryCard(navController, "Random", R.drawable.random_image, player1Name, player2Name, imageCount, isTimerEnabled, timerDuration)
        }
    }
}

@Composable
fun PlayerBox(playerName: String) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color(0xFF4E54C8), shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = playerName,
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CategoryCard(
    navController: NavController,
    categoryName: String,
    imageRes: Int,
    player1Name: String,
    player2Name: String?,
    imageCount: Int,
    isTimerEnabled: Boolean,
    timerDuration: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                val route = if (player2Name == null) {
                    "gameScreen/$categoryName/$player1Name/$imageCount/$isTimerEnabled/$timerDuration"
                } else {
                    "gameScreen/$categoryName/$player1Name/$player2Name/$imageCount/$isTimerEnabled/$timerDuration"
                }
                navController.navigate(route)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = categoryName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = categoryName,
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SettingsMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    imageCount: Int,
    onImageCountChange: (Int) -> Unit,
    isTimerEnabled: Boolean,
    onTimerToggle: () -> Unit,
    timerDuration: Int,
    onTimerDurationChange: (Int) -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Select Number of Pictures", fontWeight = FontWeight.Bold)
            Slider(
                value = imageCount.toFloat(),
                onValueChange = { onImageCountChange((it / 2).toInt() * 2) },
                valueRange = 2f..30f,
                steps = 14
            )
            Text("Selected: $imageCount", fontSize = 14.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = isTimerEnabled, onClick = onTimerToggle)
                Text("Enable Timer", fontSize = 14.sp)
            }
            if (isTimerEnabled) {
                Slider(
                    value = timerDuration.toFloat(),
                    onValueChange = { onTimerDurationChange(it.toInt()) },
                    valueRange = 10f..50f
                )
                Text("Timer: $timerDuration seconds", fontSize = 14.sp)
            }
        }
    }
}

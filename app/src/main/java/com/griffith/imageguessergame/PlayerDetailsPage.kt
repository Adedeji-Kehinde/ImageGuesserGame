package com.griffith.imageguessergame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailsPage(navController: NavController, isMultiplayer: Boolean) {
    val backgroundGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF4E54C8), Color(0xFF8F94FB))
    )

    val scrollState = rememberScrollState()
    val player1Name = remember { mutableStateOf("") }
    val player2Name = remember { mutableStateOf("") }
    val isFormValid = player1Name.value.isNotBlank() && (!isMultiplayer || player2Name.value.isNotBlank())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
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
                .padding(24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enter Player Details",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Player 1 TextField
            TextField(
                value = player1Name.value,
                onValueChange = { player1Name.value = it },
                label = { Text("Player 1 Name", color = Color.Black.copy(alpha = 0.7f)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x30FFFFFF), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp)
            )

            if (isMultiplayer) {
                Spacer(modifier = Modifier.height(16.dp))

                // Player 2 TextField
                TextField(
                    value = player2Name.value,
                    onValueChange = { player2Name.value = it },
                    label = { Text("Player 2 Name", color = Color.Black.copy(alpha = 0.7f)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x30FFFFFF), shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // "Select Category" Button
            Button(
                onClick = {
                    navController.navigate("gameCategory/${player1Name.value}/${player2Name.value}/${isMultiplayer}")
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2)),
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp)
            ) {
                Text(
                    text = "Select Category",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

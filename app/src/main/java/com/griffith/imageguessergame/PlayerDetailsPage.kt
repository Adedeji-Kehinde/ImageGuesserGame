// PlayerDetailsPage.kt
package com.griffith.imageguessergame

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailsPage(navController: NavController, isMultiplayer: Boolean) {
    val player1Name = remember { mutableStateOf("") }
    val player2Name = remember { mutableStateOf("") }
    val isFormValid = player1Name.value.isNotBlank() && (!isMultiplayer || player2Name.value.isNotBlank())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enter Player Details:",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Player 1 TextField
            TextField(
                value = player1Name.value,
                onValueChange = { player1Name.value = it },
                label = { Text("Player 1 Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            if (isMultiplayer) {
                Spacer(modifier = Modifier.height(16.dp))

                // Player 2 TextField
                TextField(
                    value = player2Name.value,
                    onValueChange = { player2Name.value = it },
                    label = { Text("Player 2 Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // "Select Category" Button, enabled only if form is valid
            Button(
                onClick = {
                    navController.navigate("gameCategory/${player1Name.value}/${player2Name.value}/${isMultiplayer}")
                },
                shape = RoundedCornerShape(12.dp),
                enabled = isFormValid,
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                Text(
                    text = "Select Category",
                    fontSize = 18.sp
                )
            }
        }
    }
}

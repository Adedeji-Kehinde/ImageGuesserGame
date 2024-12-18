package com.griffith.imageguessergame

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun HowToPlayDialog(onDismiss: () -> Unit) {
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Got it!")
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth() // Make the dialog a square box
                    .aspectRatio(1f) // Square aspect ratio
                    .verticalScroll(scrollState) // Make the content scrollable
                    .padding(16.dp)
            ) {
                Text(
                    text = "How to Play",
                    fontSize = 20.sp, // Custom font size
                    fontWeight = FontWeight.Bold // Bold text for emphasis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. Choose a category: Animals, Fruits, Logos, or Random.\n" +
                    "2. If you are playing in single-player mode, guess the object as quickly as possible.\n"+
                    "3. In multiplayer mode, take turns guessing. Each player gets a chance to guess the object.\n"+
                    "4. You have three attempts for each image:\n"+
                    "   - If you guess correctly on the first try, you earn 10 points.\n"+
                    "   - If you guess correctly on the second try, you earn 5 points.\n"+
                    "   - If you guess correctly on the last try, you earn 2 points.\n"+
                    "5. If your guess is incorrect, you may have to shake the phone to reveal more of the image and make another guess.\n"+
                    "6. The player with the highest score at the end wins!\n",
                    fontSize = 16.sp // Custom font size for regular text
                )
            }
        }
    )
}

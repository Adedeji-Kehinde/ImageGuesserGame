package com.griffith.imageguessergame

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Leaderboard(navController: NavController) {
    val databaseManager = DatabaseManager(LocalContext.current)
    val leaderboardEntries = databaseManager.getLeaderboard()
    val topThreeEntries = leaderboardEntries.sortedByDescending { it.totalPoints }.take(3)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leaderboard") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE))
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Bar Chart Section
            item {
                Text(
                    text = "Top 3 Players by Points",
                    fontSize = 20.sp,
                    color = Color(0xFF6200EE),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                BarChart(
                    data = topThreeEntries.map { it.totalPoints.toFloat() },
                    labels = topThreeEntries.map { it.playerName },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
            }

            // Table Header
            item {
                Text(
                    text = "All Player Stats",
                    fontSize = 20.sp,
                    color = Color(0xFF6200EE),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Rank", "Player", "Total", "Wins", "Losses", "Ties").forEach {
                        Text(text = it, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Table Rows
            itemsIndexed(leaderboardEntries.sortedByDescending { it.totalPoints }) { index, entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = (index + 1).toString())
                    Text(text = entry.playerName)
                    Text(text = entry.totalPoints.toString())
                    Text(text = entry.wins.toString())
                    Text(text = entry.losses.toString())
                    Text(text = entry.ties.toString())
                }
            }

            // Back to Home Button
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("HomePage") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                ) {
                    Text("Back to Home", color = Color.White)
                }
            }
        }
    }
}



// Bar Chart Component
@Composable
fun BarChart(
    data: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (data.size != 3 || labels.size != 3) {
            throw IllegalArgumentException("BarChart requires exactly 3 data points and labels.")
        }

        val maxDataValue = (data.maxOrNull() ?: 1f)

        // Relative X positions for the bars
        val barPositions = listOf(0.3f, 0.5f, 0.7f)
        val barWidth = size.width * 0.1f // Relative bar width

        data.forEachIndexed { index, value ->
            val barHeight = (value / maxDataValue) * size.height * 0.6f // Bar height relative to the maximum value
            val barCenterX = size.width * barPositions[index]

            // Draw bar
            drawRect(
                color = when (index) {
                    0 -> Color(0xFFFFD700) // First place- Gold
                    1 -> Color(0xFFC0C0C0) // Second place- Silver
                    else -> Color(0xFFCD7F32) // Third place- Bronze
                },
                topLeft = Offset(barCenterX - barWidth / 2, size.height - barHeight - 40.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
            )

            // Draw player name above the bar
            drawIntoCanvas { canvas ->
                val paint = androidx.compose.ui.graphics.Paint().asFrameworkPaint().apply {
                    color = android.graphics.Color.BLACK
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 16.dp.toPx()
                }
                canvas.nativeCanvas.drawText(
                    labels[index],
                    barCenterX,
                    size.height - barHeight - 50.dp.toPx(),
                    paint
                )
            }

            // Draw rank below the bar
            drawIntoCanvas { canvas ->
                val paint = androidx.compose.ui.graphics.Paint().asFrameworkPaint().apply {
                    color = android.graphics.Color.BLACK
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 14.dp.toPx()
                }
                val rankText = when (index) {
                    0 -> "1st" // Center (1st rank)
                    1 -> "2nd" // Left (2nd rank)
                    else -> "3rd" // Right (3rd rank)
                }
                canvas.nativeCanvas.drawText(
                    rankText,
                    barCenterX,
                    size.height + 20.dp.toPx(),
                    paint
                )
            }
        }
    }
}

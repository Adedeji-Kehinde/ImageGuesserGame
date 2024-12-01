package com.griffith.imageguessergame

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Leaderboard(navController: NavController) {
    val databaseManager = DatabaseManager(LocalContext.current)
    val leaderboardEntries = databaseManager.getLeaderboard()

    // State for sorting
    val (sortBy, setSortBy) = remember { mutableStateOf("Player") }
    val (ascending, setAscending) = remember { mutableStateOf(false) }

    // Sort the leaderboard entries based on the selected column and order
    val sortedEntries = leaderboardEntries.sortedWith(
        compareBy(
            { if (sortBy == "Player") it.playerName else null },
            { if (sortBy == "Total") it.totalPoints else null },
            { if (sortBy == "Wins") it.wins else null },
            { if (sortBy == "Losses") it.losses else null },
            { if (sortBy == "Ties") it.ties else null }
        )
    ).let { if (ascending) it else it.reversed() }

    // Get the top 3 players based on total points
    val top3Players = sortedEntries.sortedByDescending { it.totalPoints }.take(3)

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
            // Bar Chart Section (Top 3 Players)
            item {
                Text(
                    text = "Top Players by Points",
                    fontSize = 20.sp,
                    color = Color(0xFF6200EE),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                BarChart(
                    data = top3Players.map { it.totalPoints.toFloat() },
                    labels = top3Players.map { it.playerName },
                    rankings = top3Players.mapIndexed { index, _ -> (index + 1).toString() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
            }

            // Line Graph Section (Dynamic)
            item {
                Text(
                    text = "Player Performance Over Time",
                    fontSize = 20.sp,
                    color = Color(0xFF6200EE),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LineGraph(
                    data = sortedEntries.map { it.totalPoints.toFloat() },
                    labels = sortedEntries.map { it.playerName },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
            }

            // Table Header with Clickable Columns
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Player", "Total", "Wins", "Losses", "Ties").forEach { column ->
                        Text(
                            text = column,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                // Toggle sorting direction if the same column is clicked
                                if (sortBy == column) setAscending(!ascending)
                                else {
                                    setSortBy(column)
                                    setAscending(true) // Reset to ascending when switching columns
                                }
                            }
                        )
                    }
                }
            }

            // Table Rows
            itemsIndexed(sortedEntries) { index, entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
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

// Bar Chart Component (Top 3 Players)
@Composable
fun BarChart(
    data: List<Float>,
    labels: List<String>,
    rankings: List<String>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val maxDataValue = (data.maxOrNull() ?: 1f)

        // Relative X positions for the bars
        val barPositions = data.indices.map { (it + 1) * 0.25f }
        val barWidth = size.width * 0.1f // Relative bar width

        data.forEachIndexed { index, value ->
            val barHeight = (value / maxDataValue) * size.height * 0.6f
            val barCenterX = size.width * barPositions[index]

            // Draw bar
            drawRect(
                color = when (index) {
                    0 -> Color(0xFFFFD700) // Gold
                    1 -> Color(0xFFC0C0C0) // Silver
                    else -> Color(0xFFCD7F32) // Bronze
                },
                topLeft = Offset(barCenterX - barWidth / 2, size.height - barHeight - 40.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
            )

            // Draw ranking below the bar
            drawIntoCanvas { canvas ->
                val paint = androidx.compose.ui.graphics.Paint().asFrameworkPaint().apply {
                    color = android.graphics.Color.BLACK
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 16.dp.toPx()
                }
                canvas.nativeCanvas.drawText(
                    rankings[index],
                    barCenterX,
                    size.height - barHeight - 60.dp.toPx(),
                    paint
                )
            }

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
                    size.height - barHeight - 80.dp.toPx(),
                    paint
                )
            }
        }
    }
}

// Line Graph Component (Dynamic)
@Composable
fun LineGraph(
    data: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val maxDataValue = (data.maxOrNull() ?: 1f)
        val lineColor = Color(0xFF6200EE)
        val strokeWidth = 5.dp.toPx()

        val spacing = size.width / (data.size + 1)
        val points = data.mapIndexed { index, value ->
            Offset((index + 1) * spacing, size.height - (value / maxDataValue) * size.height * 0.6f)
        }

        // Draw line connecting points
        for (i in 1 until points.size) {
            drawLine(
                color = lineColor,
                start = points[i - 1],
                end = points[i],
                strokeWidth = strokeWidth
            )
        }

        // Draw player names at their respective points
        drawIntoCanvas { canvas ->
            val paint = androidx.compose.ui.graphics.Paint().asFrameworkPaint().apply {
                color = android.graphics.Color.BLACK
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 16.dp.toPx()
            }

            points.forEachIndexed { index, point ->
                canvas.nativeCanvas.drawText(
                    labels[index],
                    point.x,
                    point.y - 20.dp.toPx(),
                    paint
                )
            }
        }
    }
}

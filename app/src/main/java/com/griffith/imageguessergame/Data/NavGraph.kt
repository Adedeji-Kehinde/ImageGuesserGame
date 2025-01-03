// NavGraph.kt
package com.griffith.imageguessergame

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "HomePage"
    ) {
        // Navigate to home page
        composable("HomePage") { HomePage(navController) }

        // Navigate to player mode page
        composable("selectPlayers") { SelectPlayersPage(navController) }

        // Navigate to player details page
        composable(
            "playerDetails/{isMultiplayer}",
            arguments = listOf(navArgument("isMultiplayer") { defaultValue = false })
        ) { backStackEntry ->
            val isMultiplayer = backStackEntry.arguments?.getBoolean("isMultiplayer") ?: false
            PlayerDetailsPage(navController, isMultiplayer)
        }

        // Navigate to game category page
        composable(
            "gameCategory/{player1Name}/{player2Name}/{isMultiplayer}",
            arguments = listOf(
                navArgument("player1Name") { type = NavType.StringType },
                navArgument("player2Name") { type = NavType.StringType; defaultValue = "" },
                navArgument("isMultiplayer") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            GameCategoryPage(
                navController = navController,
                backStackEntry = backStackEntry
            )
        }

        // Navigate to game screen
        composable(
            "gameScreen/{categoryName}/{player1Name}/{player2Name}/{imageCount}/{isTimerEnabled}/{timerDuration}",
            arguments = listOf(
                navArgument("categoryName") { type = NavType.StringType },
                navArgument("player1Name") { type = NavType.StringType },
                navArgument("player2Name") { type = NavType.StringType },
                navArgument("imageCount") { type = NavType.IntType },
                navArgument("isTimerEnabled") { type = NavType.BoolType },
                navArgument("timerDuration") { type = NavType.IntType },
            )
        ) { backStackEntry ->
            GameScreenPage(navController, backStackEntry)
        }

        // Navigate to results page
        composable("resultsPage/{playerScore}/{playerScore2}/{player1Name}/{player2Name}") { backStackEntry ->
            ResultsPage(navController, backStackEntry)
        }

        //Navigate to leaderboard page
        composable("leaderboard") {
            Leaderboard(navController)
        }
    }
}

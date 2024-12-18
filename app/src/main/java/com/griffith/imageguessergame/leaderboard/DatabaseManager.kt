package com.griffith.imageguessergame

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class LeaderboardEntry(
    val playerName: String,
    val totalPoints: Int,
    val wins: Int,
    val losses: Int,
    val ties: Int,
    val gamesPlayed: Int,
    val bestScore: Int
)

class DatabaseManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ImageGuesserGame.db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_NAME = "leaderboard"
        private const val COLUMN_PLAYER_NAME = "player_name"
        private const val COLUMN_TOTAL_POINTS = "total_points"
        private const val COLUMN_WINS = "wins"
        private const val COLUMN_LOSSES = "losses"
        private const val COLUMN_TIES = "ties"
        private const val COLUMN_GAMES_PLAYED = "games_played"
        private const val COLUMN_BEST_SCORE = "best_score"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_PLAYER_NAME TEXT PRIMARY KEY,
                $COLUMN_TOTAL_POINTS INTEGER DEFAULT 0,
                $COLUMN_WINS INTEGER DEFAULT 0,
                $COLUMN_LOSSES INTEGER DEFAULT 0,
                $COLUMN_TIES INTEGER DEFAULT 0,
                $COLUMN_GAMES_PLAYED INTEGER DEFAULT 0,
                $COLUMN_BEST_SCORE INTEGER DEFAULT 0
            )
        """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    }

    fun insertOrUpdatePlayerScore(playerName: String, score: Int, result: String) {
        val db = writableDatabase

        // Check if player exists
        val cursor = db.query(TABLE_NAME, null, "$COLUMN_PLAYER_NAME = ?", arrayOf(playerName), null, null, null)
        val values = ContentValues()

        if (cursor.moveToFirst()) {
            val totalPoints = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_POINTS))
            val gamesPlayed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GAMES_PLAYED))
            val wins = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WINS))
            val losses = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOSSES))
            val ties = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TIES))
            val bestScore = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BEST_SCORE))

            // Update fields
            values.put(COLUMN_TOTAL_POINTS, totalPoints + score)
            values.put(COLUMN_GAMES_PLAYED, gamesPlayed + 1)
            values.put(COLUMN_BEST_SCORE, maxOf(bestScore, score))

            when (result) {
                "win" -> values.put(COLUMN_WINS, wins + 1)
                "loss" -> values.put(COLUMN_LOSSES, losses + 1)
                "tie" -> values.put(COLUMN_TIES, ties + 1)
            }

            db.update(TABLE_NAME, values, "$COLUMN_PLAYER_NAME = ?", arrayOf(playerName))
        } else {
            // Insert new player
            values.put(COLUMN_PLAYER_NAME, playerName)
            values.put(COLUMN_TOTAL_POINTS, score)
            values.put(COLUMN_GAMES_PLAYED, 1)
            values.put(COLUMN_BEST_SCORE, score)
            values.put(COLUMN_WINS, if (result == "win") 1 else 0)
            values.put(COLUMN_LOSSES, if (result == "loss") 1 else 0)
            values.put(COLUMN_TIES, if (result == "tie") 1 else 0)

            db.insert(TABLE_NAME, null, values)
        }

        cursor.close()
    }

    fun getLeaderboard(): List<LeaderboardEntry> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(
                COLUMN_PLAYER_NAME,
                COLUMN_TOTAL_POINTS,
                COLUMN_WINS,
                COLUMN_LOSSES,
                COLUMN_TIES,
                COLUMN_GAMES_PLAYED,
                COLUMN_BEST_SCORE
            ),
            null,
            null,
            null,
            null,
            "$COLUMN_TOTAL_POINTS DESC"
        )

        val leaderboard = mutableListOf<LeaderboardEntry>()
        while (cursor.moveToNext()) {
            leaderboard.add(
                LeaderboardEntry(
                    playerName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_NAME)),
                    totalPoints = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_POINTS)),
                    wins = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WINS)),
                    losses = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOSSES)),
                    ties = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TIES)),
                    gamesPlayed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GAMES_PLAYED)),
                    bestScore = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BEST_SCORE))
                )
            )
        }
        cursor.close()
        return leaderboard
    }
}

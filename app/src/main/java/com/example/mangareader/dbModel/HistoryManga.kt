package com.example.mangareader.dbModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryManga(
    @PrimaryKey(autoGenerate = true)
    var id : Long,
    val mangaTitle: String,
    val mangaUrl: String,
    val imageUrl : String,
    var lastReadChapterName : String,
    var lastReadChapterUrl : String,
    var lastRead : Long,
    var lastReadChapterPost : Int
)

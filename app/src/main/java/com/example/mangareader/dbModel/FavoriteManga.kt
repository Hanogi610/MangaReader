package com.example.mangareader.dbModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class FavoriteManga(
    @PrimaryKey(autoGenerate = true)
    var id : Long,
    val mangaTitle: String,
    val mangaUrl: String,
    val imageUrl : String,
    val lastAdded: Long,
    val lastChapter: String
)

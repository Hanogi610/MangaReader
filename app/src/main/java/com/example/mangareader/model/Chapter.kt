package com.example.mangareader.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class Chapter  (
    var chapterTitle : String,
    var chapterUrl : String,
    var chapterDate : String,
    var chapterView : String
): Serializable
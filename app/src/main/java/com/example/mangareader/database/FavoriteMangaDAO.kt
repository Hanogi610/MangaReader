package com.example.mangareader.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mangareader.dbModel.FavoriteManga

@Dao
interface FavoriteMangaDAO {
    @Insert
    fun insertFavoriteManga(favoriteManga: FavoriteManga) : Long

    @Delete
    fun deleteFavoriteManga(favoriteManga: FavoriteManga)

    @Query("SELECT * FROM favorite")
    fun getAllFavoriteManga(): List<FavoriteManga>

    @Query("UPDATE favorite SET lastChapter = :lastChapter WHERE mangaUrl = :mangaUrl")
    fun updateLastChapter(mangaUrl: String, lastChapter: String)

}
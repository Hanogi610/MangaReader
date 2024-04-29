package database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dbModel.FavoriteManga

@Dao
interface FavoriteMangaDAO {
    @Insert
    fun insertFavoriteManga(favoriteManga: FavoriteManga) : Long

    @Delete
    fun deleteFavoriteManga(favoriteManga: FavoriteManga)

    @Query("SELECT * FROM favorite")
    fun getAllFavoriteManga(): List<FavoriteManga>

}
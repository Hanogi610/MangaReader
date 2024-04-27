package database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dbModel.MangaDb

@Dao
interface MangaDbDAO {
    @Insert
    fun insertManga(manga: MangaDb) : Long

    @Query("SELECT * FROM manga")
    fun getAllManga(): List<MangaDb>

    @Query("SELECT * FROM manga WHERE id = :mangaId")
    fun getMangaById(mangaId: Int): MangaDb?

    @Query("UPDATE manga SET lastReadChapterPost = :lastReadChapterPost WHERE id = :mangaId")
    fun updateLastReadChapterPost(lastReadChapterPost: Int, mangaId: Int)

    @Query("DELETE FROM manga")
    fun deleteAllManga()

    @Query("DELETE FROM manga WHERE id = :mangaId")
    fun deleteMangaById(mangaId: Long)

    @Query("UPDATE manga SET isHistory = :isHistory WHERE id = :mangaId")
    fun updateIsHistory(isHistory: Boolean, mangaId: Long)

    @Query("UPDATE manga SET isFavorite = :isFavorite WHERE id = :mangaId")
    fun updateIsFavorite(isFavorite: Boolean, mangaId: Long)

    @Query("UPDATE manga SET lastRead = :lastRead WHERE id = :mangaId")
    fun updateLastRead(lastRead: String, mangaId: Long)

    @Query("SELECT * FROM manga WHERE isHistory = true")
    fun getHistoryManga(): List<MangaDb>

    @Query("SELECT * FROM manga WHERE isFavorite = true")
    fun getFavoriteManga(): List<MangaDb>

    @Query("SELECT * FROM manga WHERE mangaUrl = :url")
    fun checkMangaIfExist(url: String): MangaDb?
}
package database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dbModel.HistoryManga

@Dao
interface HistoryMangaDAO {
    @Insert
    fun insertHistoryManga(historyManga: dbModel.HistoryManga) : Long

    @Delete
    fun deleteHistoryManga(historyManga: dbModel.HistoryManga)

    @Query("SELECT * FROM history")
    fun getAllHistoryManga(): List<dbModel.HistoryManga>

    @Query("Update history SET lastRead = :lastRead , lastReadChapterName = :lastChapterName, lastReadChapterUrl = :lastChapterUrl, lastReadChapterPost = :chapterPost WHERE mangaUrl = :mangaUrl")
    fun updateHistoryManga(mangaUrl : String, lastChapterName : String, lastChapterUrl : String, lastRead : Long, chapterPost : Int)

    @Query("Update history SET lastRead = :lastRead WHERE mangaUrl = :mangaId")
    fun updateHistoryMangaLastReadById(mangaId : Long, lastRead : Long)

    @Query("SELECT * FROM history WHERE mangaUrl = :url")
    fun checkIfExistHistory(url : String) : HistoryManga?
}
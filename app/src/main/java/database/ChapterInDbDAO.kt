package database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dbModel.ChapterInDb

@Dao
interface ChapterInDbDAO {
    @Query("SELECT * FROM chapter WHERE mangaId = :mangaId")
    fun getChapterByMangaId(mangaId: Long): List<ChapterInDb>

    @Insert
    fun insertChapter(chapter: ChapterInDb) : Long

    @Query("DELETE FROM chapter WHERE mangaId = :mangaId")
    fun deleteChapterByMangaId(mangaId: Long)

    @Query("DELETE FROM chapter")
    fun deleteAllChapter()

    @Query("UPDATE chapter SET isRead = :isRead WHERE id = :chapterId")
    fun updateIsRead(isRead: Boolean, chapterId: Long)

    @Query("SELECT * FROM chapter WHERE mangaId = :mangaId AND chapterPost = :currentChapterPost + 1")
    fun getNextChapter(mangaId: Int, currentChapterPost: Int): ChapterInDb?

    @Query("SELECT * FROM chapter WHERE mangaId = :mangaId AND chapterPost = :currentChapterPost - 1")
    fun getPreviousChapter(mangaId: Int, currentChapterPost: Int): ChapterInDb?

}
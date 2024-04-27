package database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dbModel.ChapterInDb
import dbModel.MangaDb

@Database(entities = [ChapterInDb::class, MangaDb::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mangaDbDAO(): MangaDbDAO
    abstract fun chapterInDbDAO(): ChapterInDbDAO

    companion object {
        const val DATABASE_NAME = "manga_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
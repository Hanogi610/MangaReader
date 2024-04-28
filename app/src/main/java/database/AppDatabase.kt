package database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dbModel.FavoriteManga
import dbModel.HistoryManga

@Database(entities = [HistoryManga::class,FavoriteManga::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyMangaDAO(): HistoryMangaDAO
    abstract fun favoriteMangaDAO(): FavoriteMangaDAO

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
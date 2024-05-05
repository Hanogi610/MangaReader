package com.example.mangareader.fragment

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangareader.R
import com.example.mangareader.database.AppDatabase
import com.example.mangareader.dbModel.FavoriteManga
import com.example.mangareader.scraper.AsuraJsoup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class LibraryViewModel : ViewModel() {
    val mangaList = MutableLiveData<List<FavoriteManga>>()
    lateinit var INSTANCE : AppDatabase
    fun getMangas(context : Context){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                INSTANCE = AppDatabase.getInstance(context)
                mangaList.postValue(INSTANCE.favoriteMangaDAO().getAllFavoriteManga().sortedByDescending { it.lastAdded })
            }
        }
    }
    fun checkForUpdate(mangas : List<FavoriteManga>, context: Context){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val scraper = AsuraJsoup()
                for(manga in mangas){
                    var newChapters = 0
                    val chapters = scraper.getChapterList(manga.mangaUrl)
                    if(chapters.isNotEmpty()&&chapters.last().chapterUrl!=manga.lastChapter){
                        for(c in chapters){
                            if(c.chapterUrl == manga.lastChapter){
                                break
                            }
                            newChapters++
                        }
                        createNotificationChannel(context)
                        sendNotification(context, "${manga.mangaTitle} has $newChapters new chapters")
                        INSTANCE = AppDatabase.getInstance(context)
                        INSTANCE.favoriteMangaDAO().updateLastChapter(manga.mangaUrl, chapters.last().chapterUrl)
                    }else{
                        createNotificationChannel(context)
                        sendNotification(context, "${manga.mangaTitle} has no new chapters")
                    }
                }
            }
        }
    }
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MangaReader Check New Chapters Channel"
            val descriptionText = "Channel for Checking New Chapters notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("MangaReader", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun sendNotification(context: Context, s : String){
        Log.d("Notification", "Notification sent")
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.manga_icon)

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, "MangaReader")
        } else {
            Notification.Builder(context)
        }
        val builtNotification = notification.setContentTitle("New chapters check")
            .setContentText(s)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(bitmap)
            .build()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(getNotificationId(), builtNotification)
    }
    private fun getNotificationId(): Int{
        return Date().time.toInt()
    }
}
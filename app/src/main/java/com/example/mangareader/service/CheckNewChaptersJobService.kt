package com.example.mangareader.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import com.example.mangareader.R
import com.example.mangareader.database.AppDatabase
import com.example.mangareader.scraper.AsuraJsoup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class CheckNewChaptersJobService : JobService() {
    companion object {
        private const val PROGRESS_NOTIFICATION_ID = 1
    }

    override fun onStartJob(params: JobParameters): Boolean {
        Log.d("CheckNewChapters", "Job started")
        CoroutineScope(Dispatchers.IO).launch {
            val scraper = AsuraJsoup()
            val db = AppDatabase.getInstance(applicationContext)
            val favoriteMangaDao = db.favoriteMangaDAO()
            val mangaList = favoriteMangaDao.getAllFavoriteManga()
            createNotificationChannel(applicationContext)
            for ((index, manga) in mangaList.withIndex()) {
                var newChapters = 0
                val chapters = scraper.getChapterList(manga.mangaUrl)
                if(chapters.isNotEmpty()&&chapters.last().chapterUrl!=manga.lastChapter){
                    for(c in chapters){
                        if(c.chapterUrl == manga.lastChapter){
                            break
                        }
                        newChapters++
                    }
                    sendNotification(applicationContext, "${manga.mangaTitle} has $newChapters new chapters")
                    db.favoriteMangaDAO().updateLastChapter(manga.mangaUrl, chapters.last().chapterUrl)
                }else{
                    //sendNotification(applicationContext, "${manga.mangaTitle} has no new chapters")
                }
                // Calculate progress as a percentage
                val progress = ((index + 1).toDouble() / mangaList.size.toDouble() * 100).toInt()

                // Update the progress bar notification
                sendNotificationWithProgressbar(applicationContext, progress)
            }
        }
        return false
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return false
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
    private fun sendNotificationWithProgressbar(context: Context, progress: Int) {
        Log.d("Notification", "Notification with progress sent")
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.manga_icon)

        // Create RemoteViews for custom notification layout
        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_layout)
        if(progress != 100) {
            notificationLayout.setTextViewText(R.id.notification_title, "New chapters check")
            notificationLayout.setTextViewText(R.id.notification_content, "Checking for new chapters...")
        }else{
            notificationLayout.setTextViewText(R.id.notification_title, "New chapters check done")
            notificationLayout.setTextViewText(R.id.notification_content, "Checking for new chapters done!")
        }

        // Set progress to progress bar
        notificationLayout.setProgressBar(R.id.notification_progress, 100, progress, false)

        // Build notification
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, "MangaReader")
        } else {
            Notification.Builder(context)
        }
        val builtNotification = notification
            .setCustomContentView(notificationLayout) // Set custom layout
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(bitmap)
            .build()

        // Notify using NotificationManager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(CheckNewChaptersJobService.PROGRESS_NOTIFICATION_ID, builtNotification)


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
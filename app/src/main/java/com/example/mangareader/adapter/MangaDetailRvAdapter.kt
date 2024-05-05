package com.example.mangareader.adapter

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import com.example.mangareader.model.Chapter
import com.example.mangareader.model.MangaDetail
import java.util.Date

class MangaDetailRvAdapter(private val chapters: MutableList<Chapter>, private val onItemClick : (Chapter, Int) -> Unit) : RecyclerView.Adapter<MangaDetailRvAdapter.RvChapterItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvChapterItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_chapter_item, parent, false)
        return RvChapterItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RvChapterItemViewHolder, position: Int) {
        val chapter = chapters[position]
        holder.chapterTitle.text = chapter.chapterTitle
        holder.chapterDate.text = chapter.chapterDate
        holder.itemView.setOnClickListener{
            onItemClick(chapter, position)
        }
    }

    override fun getItemCount(): Int {
        return chapters.size
    }

    class RvChapterItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val chapterTitle: TextView = view.findViewById(R.id.chapter_title)
        val chapterDate: TextView = view.findViewById(R.id.chapter_date)

    }

    fun submitData(chaptersList: List<Chapter>) {
        chapters.addAll(chaptersList)
        notifyDataSetChanged()
    }

    fun updateData(newChaptersList: List<Chapter>, context: Context) {
        var newChapters = ""
        var newChapterCount = 0
        for(c in newChaptersList){
            if(c !in chapters){
                chapters.add(c)
                newChapters += c.chapterTitle + ", "
                newChapterCount++
            }
        }
        var string = ""
        if(newChapterCount > 0){
            string = "New chapters: " + newChapters.substring(0, newChapters.length - 2)
            createNotificationChannel(context)
            sendNotification(context, string)
        }else{
            string = "No new chapters"
            createNotificationChannel(context)
            sendNotification(context, string)
        }
        notifyDataSetChanged()
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
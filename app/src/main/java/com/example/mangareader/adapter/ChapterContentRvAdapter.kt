package com.example.mangareader.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.mangareader.R

class ChapterContentRvAdapter(private val chapterImages: List<String>) : RecyclerView.Adapter<ChapterContentRvAdapter.ChapterContentViewHolder>() {
    class ChapterContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.chapterImage)
        val loadingSpinner: ProgressBar = view.findViewById(R.id.loading_spinner)
        val tvNoInternet: TextView = view.findViewById(R.id.tv_no_internet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterContentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chapter_rv_item, parent, false)
        return ChapterContentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chapterImages.size
    }

    override fun onBindViewHolder(holder: ChapterContentViewHolder, position: Int) {
        val chapterImage = chapterImages[position]
        // Show the spinner
        holder.loadingSpinner.visibility = View.VISIBLE
        holder.tvNoInternet.visibility = View.GONE

        Glide.with(holder.imageView.context)
            .asBitmap()
            .load(chapterImage)
            .listener(object : RequestListener<Bitmap> {
                override fun onResourceReady(
                    resource: Bitmap,
                    model: Any, target: Target<Bitmap>,
                    dataSource: com.bumptech.glide.load.DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    val width = resource.width
                    val height = resource.height
                    Log.d("ImageSize", "Width: $width, Height: $height")

                    val displayMetrics = holder.imageView.context.resources.displayMetrics
                    val screenWidth = displayMetrics.widthPixels

                    (holder.imageView.context as Activity).runOnUiThread {
                        if (height * width > 12000000) {
                            Glide.with(holder.imageView.context)
                                .load(chapterImage)
                                .override(800, 800)
                                .into(holder.imageView)
                        } else {
                            Glide.with(holder.imageView.context)
                                .load(chapterImage)
                                .override(screenWidth, 400)
                                .into(holder.imageView)
                        }
                        holder.loadingSpinner.visibility = View.GONE
                    }
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>,
                    isFirstResource: Boolean
                ): Boolean {
                    (holder.imageView.context as Activity).runOnUiThread {
                        holder.loadingSpinner.visibility = View.GONE
                        if (!isNetworkAvailable(holder.imageView.context)) {
                            holder.tvNoInternet.visibility = View.VISIBLE
                        }
                    }
                    return false
                }
            })
            .submit()

        holder.tvNoInternet.setOnClickListener {
            holder.tvNoInternet.visibility = View.GONE
            holder.loadingSpinner.visibility = View.VISIBLE
            if (isNetworkAvailable(holder.imageView.context)) {
                onBindViewHolder(holder, position)
            } else {
                holder.loadingSpinner.visibility = View.GONE
                holder.tvNoInternet.visibility = View.VISIBLE
            }
        }
    }
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
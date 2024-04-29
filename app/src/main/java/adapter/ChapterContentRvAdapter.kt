package adapter

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
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
                        if (height * width > 11208000) {
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
                    }
                    return false
                }
            })
            .submit()
    }

}
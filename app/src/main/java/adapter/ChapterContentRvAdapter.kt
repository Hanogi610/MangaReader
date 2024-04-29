package adapter

import android.graphics.drawable.Drawable
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
        // Load the image into the ImageView
        Glide.with(holder.imageView.context)
            .load(chapterImage)
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable,
                    model: Any, target: Target<Drawable>,
                    dataSource: com.bumptech.glide.load.DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    // Hide the spinner when the image is ready
                    holder.loadingSpinner.visibility = View.GONE
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.loadingSpinner.visibility = View.GONE
                    return false
                }
            })
            .into(holder.imageView)
    }
}
package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mangareader.R

class ChapterContentRvAdapter(private val chapterImages: List<String>) : RecyclerView.Adapter<ChapterContentRvAdapter.ChapterContentViewHolder>() {
    class ChapterContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.chapterImage)
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
        // Load the image into the ImageView
        Glide.with(holder.imageView.context).load(chapterImage).into(holder.imageView)
    }
}
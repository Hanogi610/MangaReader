package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import model.Chapter
import model.MangaDetail

class MangaDetailRvAdapter(private val chapters: List<Chapter>, private val onItemClick : (Chapter, Int) -> Unit) : RecyclerView.Adapter<MangaDetailRvAdapter.RvChapterItemViewHolder>() {

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
}
package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import model.Chapter

class MangaDetailRvAdapter(private val chapters: List<Chapter>) : RecyclerView.Adapter<MangaDetailRvAdapter.RvChapterItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvChapterItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_chapter_item, parent, false)
        return RvChapterItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RvChapterItemViewHolder, position: Int) {
        val chapter = chapters[position]
        holder.bind(chapter)
    }

    override fun getItemCount(): Int {
        return chapters.size
    }

    class RvChapterItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val chapterTitle: TextView = view.findViewById(R.id.chapter_title)
        private val chapterDate: TextView = view.findViewById(R.id.chapter_date)

        fun bind(chapter: Chapter) {
            chapterTitle.text = chapter.chapterTitle
            chapterDate.text = chapter.chapterDate
        }
    }
}
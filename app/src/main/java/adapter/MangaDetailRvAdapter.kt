package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import com.example.mangareader.model.Chapter
import com.example.mangareader.model.MangaDetail

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
        Toast.makeText(context, "$newChapterCount new chapters: $newChapters", Toast.LENGTH_SHORT).show()
        notifyDataSetChanged()
    }
}
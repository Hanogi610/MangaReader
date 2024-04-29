package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import model.Chapter

class BottomSheetRvAdapter(private val chapters: List<Chapter>, private val onItemClick : (Chapter, Int) -> Unit): RecyclerView.Adapter<BottomSheetRvAdapter.viewHolder>(){
    class viewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.chapterTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bottom_sheet_rv_item, parent, false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return chapters.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.title.text = chapters[position].chapterTitle
        holder.itemView.setOnClickListener {
            onItemClick(chapters[position], position)
        }
    }
}
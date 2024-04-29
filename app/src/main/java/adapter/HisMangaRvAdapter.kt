package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mangareader.R
import dbModel.HistoryManga

class HisMangaRvAdapter(
    private val hisMangaList : List<HistoryManga>,
    private val onItemClick : (HistoryManga) -> Unit,
    private val onRemoveClick : (HistoryManga) -> Unit,
    private val onImageClick : (HistoryManga) -> Unit
) : RecyclerView.Adapter<HisMangaRvAdapter.HisMangaViewHolder>() {
    class HisMangaViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val mangaTitle = view.findViewById<TextView>(R.id.mangaTitle)
        val lastReadChapter = view.findViewById<TextView>(R.id.chapterName)
        val image = view.findViewById<ImageView>(R.id.mangaImage)
        val removeButton = view.findViewById<Button>(R.id.removeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HisMangaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.his_manga_rv_item,parent,false)
        return HisMangaViewHolder(view)
    }

    override fun getItemCount(): Int {
        return hisMangaList.size
    }

    override fun onBindViewHolder(holder: HisMangaViewHolder, position: Int) {
        val currentManga = hisMangaList[position]
        holder.mangaTitle.text = currentManga.mangaTitle
        holder.lastReadChapter.text = currentManga.lastReadChapterName
        Glide.with(holder.itemView.context).load(currentManga.imageUrl).into(holder.image)
        holder.image.setOnClickListener {
            onImageClick(currentManga)
        }
        holder.removeButton.setOnClickListener {
            onRemoveClick(currentManga)
        }
        holder.itemView.setOnClickListener {
            onItemClick(currentManga)
        }
    }
}
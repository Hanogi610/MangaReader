package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mangareader.R
import dbModel.FavoriteManga

class FavMangaRvAdapter(private val mangas : List<FavoriteManga>,
                        private val onItemClick : (FavoriteManga)->Unit,
                        private val onItemButtonClick : (FavoriteManga)->Unit
    ) : RecyclerView.Adapter<FavMangaRvAdapter.viewHolder>(){
    class viewHolder(view : View) : RecyclerView.ViewHolder(view){
        val mangaImage = view.findViewById<ImageView>(R.id.mangaImage)
        val mangaTitle = view.findViewById<TextView>(R.id.mangaTitle)
        val removeButton = view.findViewById<Button>(R.id.removeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fav_manga_rv_item, parent, false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return mangas.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val manga = mangas[position]
        holder.mangaTitle.text = manga.mangaTitle
        Glide.with(holder.itemView.context).load(manga.imageUrl).into(holder.mangaImage)
        holder.removeButton.setOnClickListener {
            onItemButtonClick(manga)
        }
        holder.itemView.setOnClickListener(){
            onItemClick(manga)
        }
    }
}
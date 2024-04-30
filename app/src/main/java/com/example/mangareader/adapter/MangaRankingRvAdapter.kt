package com.example.mangareader.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mangareader.R
import com.example.mangareader.model.MangaByRank

class MangaRankingRvAdapter(
    private val mangas : List<MangaByRank>,
    private val itemClickListener: (MangaByRank) -> Unit
) : RecyclerView.Adapter<MangaRankingRvAdapter.MangaRankingViewHolder>() {
    class MangaRankingViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val ranking_tv = view.findViewById<TextView>(R.id.ranking_tv)
        val image = view.findViewById<ImageView>(R.id.manga_iv)
        val title_tv = view.findViewById<TextView>(R.id.title_tv)
        val genre_tv = view.findViewById<TextView>(R.id.genre_tv)
        val rating_tv = view.findViewById<RatingBar>(R.id.rating_rb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaRankingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ranking_rv_item, parent, false)
        return MangaRankingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mangas.size
    }

    override fun onBindViewHolder(holder: MangaRankingViewHolder, position: Int) {
        holder.ranking_tv.text = mangas[position].rank
        holder.title_tv.text = mangas[position].title
        holder.genre_tv.text = mangas[position].genre
        holder.rating_tv.rating = mangas[position].rating.toFloat()
        holder.itemView.setOnClickListener {
            itemClickListener(mangas[position])
        }
        Glide.with(holder.itemView.context)
            .load(mangas[position].imageUrl)
            .into(holder.image)
    }
}
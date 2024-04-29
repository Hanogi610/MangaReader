package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mangareader.R
import model.Manga

class HomeChildFragmentRvAdapter (private var mangaList: MutableList<Manga>,
                                  private val onItemClick : (Manga) -> Unit
    ) : RecyclerView.Adapter<HomeChildFragmentRvAdapter.HomeChildFragmentViewHolder>() {
    class HomeChildFragmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.iv_home_child_fragment_rv_item)
        val titleTextView: TextView = view.findViewById(R.id.tv_home_child_fragment_rv_item_title)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeChildFragmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_child_fragment_rv_item, parent, false)
        return HomeChildFragmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeChildFragmentViewHolder, position: Int) {
        val manga = mangaList[position]
        holder.titleTextView.text = manga.title
        Glide.with(holder.imageView.context).load(manga.imageUrl).into(holder.imageView)
        holder.itemView.setOnClickListener {
            onItemClick(manga)
        }
    }

    override fun getItemCount(): Int {
        return mangaList.size
    }

    fun updateData(newMangaList: List<Manga>) {
        val oldSize = mangaList.size
        mangaList.addAll(newMangaList)
        notifyItemRangeInserted(oldSize, newMangaList.size)
    }

    fun submitData(mangas : List<Manga>){
        mangaList = mangas.toMutableList()
        notifyDataSetChanged()
    }

}

package adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mangareader.R
import model.HomepageManga

class HomeChildFragmentRvAdapter (private val mangaList: List<HomepageManga>,
    private val onItemClick : (HomepageManga) -> Unit
    ) : RecyclerView.Adapter<HomeChildFragmentViewHolder>() {

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

    fun addMangas(mangas: List<HomepageManga>) {
        val startPosition = mangaList.size
        mangaList.toMutableList().addAll(mangas)
        notifyItemRangeInserted(startPosition, mangas.size)
    }

    fun submitData(mangas: List<HomepageManga>) {
        mangaList.toMutableList().clear()
        mangaList.toMutableList().addAll(mangas)
        notifyDataSetChanged()
    }

}
class HomeChildFragmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val imageView: ImageView = view.findViewById(R.id.iv_home_child_fragment_rv_item)
    val titleTextView: TextView = view.findViewById(R.id.tv_home_child_fragment_rv_item_title)
}
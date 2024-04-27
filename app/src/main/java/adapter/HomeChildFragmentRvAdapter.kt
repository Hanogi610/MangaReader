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
import com.example.mangareader.activity.MangaDetailActivity
import model.HomepageManga

class HomeChildFragmentRvAdapter (private val mangaList: List<HomepageManga>) : RecyclerView.Adapter<HomeChildFragmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeChildFragmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_child_fragment_rv_item, parent, false)
        return HomeChildFragmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeChildFragmentViewHolder, position: Int) {
        val manga = mangaList[position]
        holder.titleTextView.text = manga.title
        Glide.with(holder.imageView.context).load(manga.imageUrl).into(holder.imageView)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MangaDetailActivity::class.java)
            intent.putExtra("url", manga.url)
            holder.itemView.context.startActivity(intent)
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
}
class HomeChildFragmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val imageView: ImageView = view.findViewById(R.id.iv_home_child_fragment_rv_item)
    val titleTextView: TextView = view.findViewById(R.id.tv_home_child_fragment_rv_item_title)
}
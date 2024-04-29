package fragment

import adapter.HomeChildFragmentRvAdapter
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import com.example.mangareader.activity.MangaDetailActivity
import model.Manga


class LatestMangaFragment : Fragment() {

    lateinit var recyclerView : RecyclerView

    companion object {
        fun newInstance() = LatestMangaFragment()
    }

    private val viewModel: LatestMangaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        println("LatestMangaFragment.onCreateView()")
        return inflater.inflate(R.layout.fragment_latest_manga, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_latest_manga)
        recyclerView.layoutManager = GridLayoutManager(context, 4)

        viewModel.mangaList.observe(viewLifecycleOwner, Observer { mangas ->

            val adapter = HomeChildFragmentRvAdapter(mangas) {
                val intent = Intent(context, MangaDetailActivity::class.java)
                intent.putExtra("url",it.url)
                intent.putExtra("img",it.imageUrl)
                startActivity(intent)
            }
            recyclerView.adapter = adapter
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

            }
        })

        viewModel.fetchLatestMangas()

    }

}
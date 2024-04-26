package fragment

import adapter.HomeChildFragmentRvAdapter
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.HomepageManga
import scraper.NettruyenJsoup

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
            val adapter = HomeChildFragmentRvAdapter(mangas)
            recyclerView.adapter = adapter
        })

//        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val layoutManager = recyclerView.layoutManager as GridLayoutManager
//                val totalItemCount = layoutManager.itemCount
//                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
//
//                if (totalItemCount -1 == lastVisibleItem) {
//                    viewModel.fetchLatestMangas()
//                }
//            }
//        })

        viewModel.fetchLatestMangas()

    }

}
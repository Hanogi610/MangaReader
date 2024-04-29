package fragment

import adapter.HomeChildFragmentRvAdapter
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import com.example.mangareader.activity.MangaDetailActivity
import model.Manga


class LatestMangaFragment : Fragment() {

    lateinit var recyclerView : RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var nestedScrollView: NestedScrollView
    var currentPage = 1

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

        progressBar = view.findViewById(R.id.loading_spinner)
        nestedScrollView = view.findViewById(R.id.scrollView)
        recyclerView = view.findViewById(R.id.rv_latest_manga)
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        var mangaList = mutableListOf<Manga>()
        val adapter = HomeChildFragmentRvAdapter(mangaList) {
            val intent = Intent(context, MangaDetailActivity::class.java)
            intent.putExtra("url",it.url)
            intent.putExtra("img",it.imageUrl)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        viewModel.mangaList.observe(viewLifecycleOwner, Observer { mangas ->
            if(mangaList.isEmpty()){
                mangaList.addAll(mangas)
                adapter.submitData(mangaList)
            }else{
                adapter.updateData(mangas)
            }
            progressBar.visibility = View.GONE
        })

        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if(scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight){
                progressBar.visibility = View.VISIBLE
                if(currentPage<=6){
                    progressBar.visibility = View.VISIBLE
                    viewModel.fetchLatestMangas(currentPage++)
                }else{
                    Toast.makeText(context, "No more data", Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.fetchLatestMangas(currentPage++)

    }

}
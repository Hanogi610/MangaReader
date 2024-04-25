package fragment

import adapter.HomeChildFragmentRvAdapter
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import model.HomepageManga

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

        println("LatestMangaFragment.onViewCreated()")
        recyclerView = view.findViewById(R.id.rv_latest_manga)
        var mangaList = createDummyData()
        val adapter = HomeChildFragmentRvAdapter(mangaList)
        recyclerView.adapter = adapter
    }

    fun createDummyData(): List<HomepageManga> {
        val dummyData = mutableListOf<HomepageManga>()

        for (i in 1..10) {
            val manga = HomepageManga()
            manga.title = "Manga Title $i"
            manga.latestChapter = "Chapter $i"
            manga.imageUrl = "https://example.com/image_$i.jpg"
            manga.url = "https://example.com/manga_$i"
            manga.viewcount = "Views: ${i * 100}"
            manga.commentcount = "Comments: ${i * 10}"
            manga.favcount = "Favorites: ${i * 5}"

            dummyData.add(manga)
        }

        return dummyData
    }

}
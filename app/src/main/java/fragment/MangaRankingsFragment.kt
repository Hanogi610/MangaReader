package fragment

import adapter.HomeChildFragmentRvAdapter
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R

class MangaRankingsFragment : Fragment() {

    data class StatusOption(val name: String, val value: Int) {
        override fun toString(): String {
            return name
        }
    }

    data class SortOption(val name: String, val value: Int) {
        override fun toString(): String {
            return name
        }
    }

    lateinit var statusSpinner: Spinner
    lateinit var sortSpinner: Spinner
    lateinit var recyclerView: RecyclerView
    private val viewModel: MangaRankingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manga_rankings, container, false)

        statusSpinner = view.findViewById(R.id.status_spinner)
        sortSpinner = view.findViewById(R.id.sort_spinner)
        recyclerView = view.findViewById(R.id.rv_manga_rankings)

        // Set up your Spinners
        val statusOptions = arrayOf(StatusOption("All", -1), StatusOption("Completed", 2), StatusOption("Ongoing", 1))
        val sortOptions = arrayOf(
            SortOption("Top All", 10), SortOption("Top Week", 12), SortOption("Top Month", 11),
            SortOption("Top Day", 13), SortOption("Top Favourite", 20), SortOption("Top Comment", 25),
            SortOption("Chapter Count", 30)
        )

        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusOptions)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = statusAdapter

        val sortAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = sortAdapter

        // Set up Spinner selection listeners
        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                fetchManga()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                fetchManga()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        // Set up your RecyclerView here...
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        viewModel.mangaList.observe(viewLifecycleOwner, Observer { mangaList ->
            val adapter = HomeChildFragmentRvAdapter(mangaList)
            recyclerView.adapter = adapter
        })
        return view
    }
    private fun fetchManga() {
        val status = (statusSpinner.selectedItem as StatusOption).value
        val sort = (sortSpinner.selectedItem as SortOption).value
        val url = "https://www.nettruyenvv.com/tim-truyen?status=$status&sort=$sort"
        viewModel.fetchManga(url)
    }
}
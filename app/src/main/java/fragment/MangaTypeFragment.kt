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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import model.Genre

class MangaTypeFragment : Fragment() {

    lateinit var genreSpinner: Spinner
    lateinit var recyclerView: RecyclerView


    companion object {
        fun newInstance() = MangaTypeFragment()
    }

    private val viewModel: MangaTypeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
        viewModel.getGenre()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_manga_type, container, false)

        genreSpinner = view.findViewById(R.id.genre_spinner)
        recyclerView = view.findViewById(R.id.manga_type_recycler_view)

        // Set up your Spinner
        viewModel.genreList.observe(viewLifecycleOwner, Observer {
            val genreAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it)
            genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genreSpinner.adapter = genreAdapter
        })

        genreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                fetchManga()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // Set up your RecyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        viewModel.mangaList.observe(viewLifecycleOwner, Observer { mangas ->
            val adapter = HomeChildFragmentRvAdapter(mangas)
            recyclerView.adapter = adapter
        })

        fetchManga()

        return view
    }
    fun fetchManga() {
        val selectedItem = genreSpinner.selectedItem
        if (selectedItem != null) {
            val value = (selectedItem as Genre).url
            if (value != null) {
                viewModel.fetchManga(value)
            }
        }
    }
}
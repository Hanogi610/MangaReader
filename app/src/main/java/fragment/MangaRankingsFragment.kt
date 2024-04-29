package fragment


import adapter.MangaRankingRvAdapter
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import com.example.mangareader.activity.MangaDetailActivity

class MangaRankingsFragment : Fragment() {

    data class StatusOption(val name: String, val value: Int) {
        override fun toString(): String {
            return name
        }
    }

    lateinit var statusSpinner: Spinner
    lateinit var recyclerView: RecyclerView
    private val viewModel: MangaRankingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manga_rankings, container, false)

        statusSpinner = view.findViewById(R.id.status_spinner)
        recyclerView = view.findViewById(R.id.rv_manga_rankings)

        // Set up your Spinners
        val statusOptions = arrayOf(StatusOption("Weekly", 1), StatusOption("Monthly", 2), StatusOption("All time", 3))

        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusOptions)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = statusAdapter
        statusSpinner.setSelection(0)

        // Set up Spinner selection listeners
        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                fetchManga()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        // Set up your RecyclerView here...
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewModel.mangaList.observe(viewLifecycleOwner, Observer { mangaList ->
            val adapter = MangaRankingRvAdapter(mangaList){
                val intent = Intent(context, MangaDetailActivity::class.java)
                intent.putExtra("url", it.url)
                intent.putExtra("img", it.imageUrl)
                startActivity(intent)
            }
            recyclerView.adapter = adapter
        })

        fetchManga()

        return view
    }
    private fun fetchManga() {
        val status = (statusSpinner.selectedItem as StatusOption).value
        val url = "https://asuratoon.com/"
        viewModel.fetchManga(url,status)
    }
}
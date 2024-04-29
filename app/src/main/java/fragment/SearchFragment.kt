package fragment

import adapter.HomeChildFragmentRvAdapter
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Query
import com.example.mangareader.R
import com.example.mangareader.activity.MangaDetailActivity
import com.google.android.material.textfield.TextInputEditText

class SearchFragment : Fragment() {

    companion object {
        fun newInstance() = SearchFragment()
    }

    private val viewModel: SearchViewModel by viewModels()
    lateinit var adapter : HomeChildFragmentRvAdapter
    lateinit var rv : RecyclerView
    lateinit var textInput : TextInputEditText
    lateinit var query: String
    lateinit var loadingSpinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingSpinner = view.findViewById(R.id.loading_spinner)
        textInput = view.findViewById(R.id.search_input_et)
        rv = view.findViewById(R.id.search_rv)
        rv.layoutManager = GridLayoutManager(context, 4)
        textInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                query = textInput.text.toString()
                viewModel.searchManga(query)
                loadingSpinner.visibility = View.VISIBLE
                return@setOnEditorActionListener true
            }
            false
        }

        viewModel.searchResult.observe(viewLifecycleOwner, Observer{mangas->
            loadingSpinner.visibility = View.GONE
            adapter = HomeChildFragmentRvAdapter(mangas.toMutableList()) {
                val intent = Intent(context, MangaDetailActivity::class.java)
                intent.putExtra("url",it.url)
                intent.putExtra("img",it.imageUrl)
                startActivity(intent)
            }
            rv.adapter = adapter
        })
    }
}
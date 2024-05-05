package com.example.mangareader.fragment

import com.example.mangareader.adapter.FavMangaRvAdapter
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import com.example.mangareader.activity.MangaDetailActivity
import com.example.mangareader.database.AppDatabase
import com.example.mangareader.dbModel.FavoriteManga
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryFragment : Fragment() {

    companion object {
        fun newInstance() = LibraryFragment()
    }

    private val viewModel: LibraryViewModel by viewModels()
    lateinit var favoriteMangaAdapter : FavMangaRvAdapter
    lateinit var rv : RecyclerView
    lateinit var INSTANCE : AppDatabase
    lateinit var toolbar : MaterialToolbar
    lateinit var editText : TextInputEditText
    lateinit var filter : ImageButton
    lateinit var alphabet : TextView
    lateinit var time : TextView
    lateinit var bottomSheetDialog : BottomSheetDialog
    private var isAlphabeticalAscending = false
    private var isTimeAscending = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        INSTANCE = AppDatabase.getInstance(requireContext())

        // TODO: Use the ViewModel
        viewModel.getMangas(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        INSTANCE = AppDatabase.getInstance(requireContext())

        toolbar = view.findViewById(R.id.libraryToolbar)
        editText = toolbar.findViewById(R.id.searchEditText)
        filter = toolbar.findViewById(R.id.filterButton)

        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.fav_bottom_sheet_layout)
        alphabet = bottomSheetDialog.findViewById(R.id.alphabetical_order)!!
        time = bottomSheetDialog.findViewById(R.id.time_added)!!
        time.isSelected = true
        filter.setOnClickListener {
            bottomSheetDialog.show()
        }

        alphabet.setOnClickListener {
            if(alphabet.isSelected) isAlphabeticalAscending = !isAlphabeticalAscending
            alphabet.isSelected = true
            time.isSelected = false
            isTimeAscending = false
            time.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_downward, 0)
            if (!isAlphabeticalAscending) {
                // If currently in ascending order, sort in descending order
                val filtered = viewModel.mangaList.value?.sortedBy { it.mangaTitle }
                favoriteMangaAdapter.updateList(filtered ?: listOf())
                alphabet.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_downward, 0)
            } else {
                // If currently in descending order, sort in ascending order
                val filtered = viewModel.mangaList.value?.sortedByDescending { it.mangaTitle }
                favoriteMangaAdapter.updateList(filtered ?: listOf())
                alphabet.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_upward, 0)
            }
            bottomSheetDialog.dismiss()
        }
        time.setOnClickListener() {
            if(time.isSelected) isTimeAscending = !isTimeAscending
            alphabet.isSelected = false
            time.isSelected = true
            isAlphabeticalAscending = false
            alphabet.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_downward, 0)
            if (!isTimeAscending) {
                // If currently in ascending order, sort in descending order
                val filtered = viewModel.mangaList.value?.sortedByDescending { it.lastAdded}
                favoriteMangaAdapter.updateList(filtered ?: listOf())
                time.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_downward, 0)
            } else {
                // If currently in descending order, sort in ascending order
                val filtered = viewModel.mangaList.value?.sortedBy { it.lastAdded}
                favoriteMangaAdapter.updateList(filtered ?: listOf())
                time.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_upward, 0)
            }
            bottomSheetDialog.dismiss()
        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val filteredMangas = viewModel.mangaList.value?.filter { manga ->
                    manga.mangaTitle.contains(s.toString(), ignoreCase = true)
                }

                // Update your adapter with the filtered list
                favoriteMangaAdapter.updateList(filteredMangas ?: listOf())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })



        rv = view.findViewById(R.id.libraryRv)
        rv.layoutManager = LinearLayoutManager(requireContext())
        viewModel.mangaList.observe(viewLifecycleOwner, Observer {mangas ->
            favoriteMangaAdapter = FavMangaRvAdapter(mangas,
                onItemClick = { favoriteManga ->
                    val intent = Intent(context, MangaDetailActivity::class.java)
                    intent.putExtra("url",favoriteManga.mangaUrl)
                    intent.putExtra("img",favoriteManga.imageUrl)
                    startActivity(intent)
                },
                onItemButtonClick = { favoriteManga ->
                    // Handle button click here
                    viewModel.viewModelScope.launch {
                        withContext(Dispatchers.IO){
                            INSTANCE.favoriteMangaDAO().deleteFavoriteManga(favoriteManga)
                        }
                    }
                    viewModel.getMangas(requireContext())
                }
            )
            rv.adapter = favoriteMangaAdapter
        })

        viewModel.getMangas(requireContext())

    }

    override fun onResume() {
        super.onResume()
        // Fetch the latest data from the database
        viewModel.getMangas(requireContext())
    }
}
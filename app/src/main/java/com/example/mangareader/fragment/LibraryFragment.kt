package com.example.mangareader.fragment

import adapter.FavMangaRvAdapter
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import com.example.mangareader.activity.MangaDetailActivity
import com.example.mangareader.database.AppDatabase
import com.example.mangareader.dbModel.FavoriteManga
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
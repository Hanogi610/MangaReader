package com.example.mangareader.fragment

import com.example.mangareader.adapter.HisMangaRvAdapter
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
import com.example.mangareader.activity.ChapContentActivity
import com.example.mangareader.activity.MangaDetailActivity
import com.example.mangareader.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.mangareader.model.Chapter

class HistoryFragment : Fragment() {

    companion object {
        fun newInstance() = HistoryFragment()
    }

    private val viewModel: HistoryViewModel by viewModels()
    lateinit var INSTANCE : AppDatabase
    lateinit var rv : RecyclerView
    lateinit var adapter: HisMangaRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
        viewModel.getMangas(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = view.findViewById(R.id.historyRecyclerView)
        rv.layoutManager = LinearLayoutManager(requireContext())

        INSTANCE = AppDatabase.getInstance(requireContext())

        viewModel.mangas.observe(viewLifecycleOwner, Observer{ mangas ->
            adapter = HisMangaRvAdapter(mangas,
                onItemClick = { historyManga ->
                    val intent = Intent(context, ChapContentActivity::class.java)
                    intent.putExtra("mangaUrl",historyManga.mangaUrl)
                    intent.putExtra("chapterPost",historyManga.lastReadChapterPost)
                    val chapterList = arrayListOf<Chapter>()
                    intent.putExtra("chapterList",chapterList)
                    intent.putExtra("chapUrl",historyManga.lastReadChapterUrl)
                    intent.putExtra("chapTitle",historyManga.lastReadChapterName)
                    startActivity(intent)
                },
                onRemoveClick = { historyManga ->
                    viewModel.viewModelScope.launch {
                        withContext(Dispatchers.IO){
                            INSTANCE.historyMangaDAO().deleteHistoryManga(historyManga)
                        }
                    }
                    viewModel.getMangas(requireContext())
                },
                onImageClick = { historyManga ->
                    val intent = Intent(context, MangaDetailActivity::class.java)
                    intent.putExtra("url",historyManga.mangaUrl)
                    intent.putExtra("img",historyManga.imageUrl)
                    startActivity(intent)
                }
            )
            rv.adapter = adapter
        })

        viewModel.getMangas(requireContext())

    }

    override fun onResume() {
        super.onResume()
        // Fetch the latest data from the database
        viewModel.getMangas(requireContext())
    }

}
package com.example.mangareader.fragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.mangareader.model.Manga
import com.example.mangareader.scraper.AsuraJsoup
import com.example.mangareader.scraper.MangaParkJsoup
import com.example.mangareader.scraper.NettruyenJsoup


class LatestMangaViewModel : ViewModel() {

    val mangaList = MutableLiveData<List<Manga>>()
    fun fetchLatestMangas(page : Int) {
        Log.d("LatestMangaViewModel", "fetchLatestMangas()")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val scraper = AsuraJsoup()
                val mangas = scraper.getLatestManga("https://asuratoon.com/page/"+page)
                mangaList.postValue(mangas)
                Log.d("LatestMangaViewModel", "fetchLatestMangas() success, ${mangas.size} mangas fetched")
            } catch (e: Exception) {
                Log.e("LatestMangaViewModel", "fetchLatestMangas() failed", e)
                // You can post a specific value to mangaList to indicate that the fetch operation failed
                mangaList.postValue(emptyList())
            }
        }
    }


}
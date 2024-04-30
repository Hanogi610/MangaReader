package com.example.mangareader.fragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.mangareader.model.MangaByRank
import com.example.mangareader.scraper.AsuraJsoup

class MangaRankingsViewModel : ViewModel() {
    val mangaList = MutableLiveData<List<MangaByRank>>()

    fun fetchManga(url: String, type : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val scraper = AsuraJsoup()
                val mangas = scraper.getMangaByRankingType(url,type)
                mangaList.postValue(mangas)
            } catch (e: Exception) {
                Log.e("MangaRankingsViewModel", "fetchManga() failed", e)
                // You can post a specific value to mangaList to indicate that the fetch operation failed
                mangaList.postValue(emptyList())
            }
        }
    }
}
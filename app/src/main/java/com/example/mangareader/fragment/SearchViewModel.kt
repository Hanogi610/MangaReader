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

class SearchViewModel : ViewModel() {
    val searchResult = MutableLiveData<List<Manga>>()

    fun searchManga(query: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val scraper = AsuraJsoup()
                    val mangas = scraper.getMangaByName(query)
                    searchResult.postValue(mangas)
                } catch (e: Exception) {
                    Log.e("SearchViewModel", "searchManga() failed", e)
                    // You can post a specific value to searchResult to indicate that the fetch operation failed
                    searchResult.postValue(emptyList())
                }
            }
        }
    }

}
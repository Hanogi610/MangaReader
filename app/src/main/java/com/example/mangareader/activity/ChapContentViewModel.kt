package com.example.mangareader.activity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.mangareader.model.Chapter
import com.example.mangareader.scraper.AsuraJsoup
import com.example.mangareader.scraper.MangaParkJsoup
import com.example.mangareader.scraper.NettruyenJsoup

class ChapContentViewModel : ViewModel() {
    val chapContent = MutableLiveData<List<String>>()
    val chapterList = MutableLiveData<List<Chapter>>()

    fun getChapterImages(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val scraper = AsuraJsoup()
                val images = scraper.getChapterImages(url)
                chapContent.postValue(images)
                Log.d("ChapContentViewModel", "getChapterImages() success")
            } catch (e: Exception) {
                Log.e("ChapContentViewModel", "getChapterImages() failed", e)
                // You can post a specific value to chapContent to indicate that the fetch operation failed
                chapContent.postValue(emptyList())
            }
        }
    }

    fun getChapterList(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val scraper = AsuraJsoup()
                val chapters = scraper.getChapterList(url)
                chapterList.postValue(chapters)
                Log.d("ChapContentViewModel", "getChapterList() success")
            } catch (e: Exception) {
                Log.e("ChapContentViewModel", "getChapterList() failed", e)
                // You can post a specific value to chapterList to indicate that the fetch operation failed
                chapterList.postValue(emptyList())
            }
        }
    }
}
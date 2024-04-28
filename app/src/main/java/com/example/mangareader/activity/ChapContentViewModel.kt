package com.example.mangareader.activity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Chapter
import scraper.AsuraJsoup
import scraper.MangaParkJsoup
import scraper.NettruyenJsoup

class ChapContentViewModel : ViewModel() {
    val chapContent = MutableLiveData<List<String>>()
    val chapterList = MutableLiveData<List<Chapter>>()

    fun getChapterImages(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val scraper = AsuraJsoup()
            val images = scraper.getChapterImages(url)
            chapContent.postValue(images)
            Log.d("ChapContentViewModel", "getChapterImages() success")
        }
    }

    fun getChapterList(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val scraper = AsuraJsoup()
            val chapters = scraper.getChapterList(url)
            chapterList.postValue(chapters)
            Log.d("ChapContentViewModel", "getChapterList() success")
        }
    }
}
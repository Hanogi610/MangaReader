package com.example.mangareader.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.MangaDetail
import scraper.NettruyenJsoup

class MangaDetailViewModel : ViewModel() {
    val mangaDetail = MutableLiveData<MangaDetail>()

    fun getMangaDetail(url: String) {
        viewModelScope.launch (Dispatchers.IO){
            val scraper = NettruyenJsoup()
            val detail = scraper.getMangaDetail(url)
            mangaDetail.postValue(detail)
        }
    }
}
package com.example.mangareader.activity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.MangaDetail
import scraper.AsuraJsoup
import scraper.MangaParkJsoup
import scraper.NettruyenJsoup
import kotlin.coroutines.cancellation.CancellationException

class MangaDetailViewModel : ViewModel() {
    val mangaDetail = MutableLiveData<MangaDetail>()

    fun getMangaDetail(url: String) {
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                val scraper = AsuraJsoup()
                val detail = scraper.getMangaDetail(url)
                mangaDetail.postValue(detail)
                Log.d("MangaDetailViewModel", "getMangaDetail() success")
            }

        }
    }

}
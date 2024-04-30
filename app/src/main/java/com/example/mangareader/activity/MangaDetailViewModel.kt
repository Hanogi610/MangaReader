package com.example.mangareader.activity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.mangareader.model.Chapter
import com.example.mangareader.model.MangaDetail
import com.example.mangareader.scraper.AsuraJsoup
import com.example.mangareader.scraper.MangaParkJsoup
import com.example.mangareader.scraper.NettruyenJsoup
import kotlin.coroutines.cancellation.CancellationException

class MangaDetailViewModel : ViewModel() {
    val mangaDetail = MutableLiveData<MangaDetail>()
    val chapterList = MutableLiveData<List<Chapter>>()

    fun getMangaDetail(url: String) {
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                try {
                    val scraper = AsuraJsoup()
                    val detail = scraper.getMangaDetail(url)
                    mangaDetail.postValue(detail)
                    Log.d("MangaDetailViewModel", "getMangaDetail() success")
                } catch (e: Exception) {
                    Log.e("MangaDetailViewModel", "getMangaDetail() failed", e)
                    // You can post a specific value to mangaDetail to indicate that the fetch operation failed
                    mangaDetail.postValue(
                        MangaDetail(
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        arrayListOf<Chapter>(),
                        ""
                        )
                    )
                }
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
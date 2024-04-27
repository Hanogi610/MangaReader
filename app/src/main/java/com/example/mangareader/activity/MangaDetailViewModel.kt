package com.example.mangareader.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import database.AppDatabase
import dbModel.MangaDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

//    fun insertMangaToDatabase(mangaDetail: MangaDetail) {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                val mangaDb = MangaDb(
//                    0,
//                    mangaDetail.mangaTitle!!,
//                    mangaDetail.mangaUrl!!,
//                    mangaDetail.mangaSynopsis!!,
//                    mangaDetail.mangaStatus!!,
//                    mangaDetail.mangaAuthor!!,
//                    mangaDetail.mangaGenre!!,
//                    mangaDetail.mangaViewCount!!,
//                    mangaDetail.rating!!,
//                    true,
//                    true,
//                    ""
//                )
//                INSTANCE.mangaDbDAO().insertManga(mangaDb)
//            }
//        }
//    }
}
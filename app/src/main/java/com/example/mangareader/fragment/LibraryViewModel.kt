package com.example.mangareader.fragment

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangareader.database.AppDatabase
import com.example.mangareader.dbModel.FavoriteManga
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryViewModel : ViewModel() {
    val mangaList = MutableLiveData<List<FavoriteManga>>()
    fun getMangas(context : Context){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val INSTANCE = AppDatabase.getInstance(context)
                mangaList.postValue(INSTANCE.favoriteMangaDAO().getAllFavoriteManga().sortedByDescending { it.lastAdded })
            }
        }
    }
}
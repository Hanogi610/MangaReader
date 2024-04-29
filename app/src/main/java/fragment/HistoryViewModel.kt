package fragment

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import database.AppDatabase
import dbModel.HistoryManga
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryViewModel : ViewModel() {
    var mangas = MutableLiveData<List<HistoryManga>>()

    fun getMangas(context : Context){
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                val INSTANCE = AppDatabase.getInstance(context)
                mangas.postValue(INSTANCE.historyMangaDAO().getAllHistoryManga())
            }
        }
    }

}
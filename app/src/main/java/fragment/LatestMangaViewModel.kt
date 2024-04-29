package fragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.Manga
import scraper.AsuraJsoup
import scraper.MangaParkJsoup
import scraper.NettruyenJsoup


class LatestMangaViewModel : ViewModel() {

    val mangaList = MutableLiveData<List<Manga>>()
    fun fetchLatestMangas(page : Int) {
        Log.d("LatestMangaViewModel", "fetchLatestMangas()")
        viewModelScope.launch(Dispatchers.IO) {
            val scraper = AsuraJsoup()
            val mangas = scraper.getLatestManga("https://asuratoon.com/page/"+page)
            mangaList.postValue(mangas)
            Log.d("LatestMangaViewModel", "fetchLatestMangas() success, ${mangas.size} mangas fetched")
        }
    }


}
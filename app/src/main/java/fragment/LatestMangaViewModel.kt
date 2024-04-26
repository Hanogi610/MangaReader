package fragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.HomepageManga
import scraper.NettruyenJsoup

class LatestMangaViewModel : ViewModel() {

    val mangaList = MutableLiveData<List<HomepageManga>>()
    var currentPage = 1
    fun fetchLatestMangas() {
        Log.d("LatestMangaViewModel", "fetchLatestMangas()")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("LatestMangaViewModel", "fetchLatestMangas() try")
                val scraper = NettruyenJsoup()
                val mangas = scraper.getLatestMangas("https://nettruyenco.vn/?page="+currentPage)
                Log.d("LatestMangaViewModel", "fetchLatestMangas() mangas.size = ${mangas.size}")
                mangaList.postValue(mangas)
                Log.d("LatestMangaViewModel", "fetchLatestMangas() mangaList.size = ${mangaList.value?.size}")
                currentPage++
            } catch (e: Exception) {
                Log.e("LatestMangaViewModel", "fetchLatestMangas() error: ${e.message}")
            }
        }
    }
}
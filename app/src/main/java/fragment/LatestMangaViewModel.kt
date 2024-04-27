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
                val scraper = NettruyenJsoup()
                val mangas = scraper.getLatestMangas("https://www.nettruyenvv.com/?page="+currentPage)
                mangaList.postValue(mangas)
                currentPage++
            } catch (e: Exception) {
                Log.e("LatestMangaViewModel", "fetchLatestMangas() error: ${e.message}")
            }
        }
    }
}
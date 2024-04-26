package fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.HomepageManga
import scraper.NettruyenJsoup

class MangaRankingsViewModel : ViewModel() {
    val mangaList = MutableLiveData<List<HomepageManga>>()

    fun fetchManga(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val scraper = NettruyenJsoup()
            val mangas = scraper.getLatestMangas(url)
            mangaList.postValue(mangas)
        }
    }
}
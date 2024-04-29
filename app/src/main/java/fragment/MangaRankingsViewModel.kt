package fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.MangaByRank
import scraper.AsuraJsoup

class MangaRankingsViewModel : ViewModel() {
    val mangaList = MutableLiveData<List<MangaByRank>>()

    fun fetchManga(url: String, type : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val scraper = AsuraJsoup()
            val mangas = scraper.getMangaByRankingType(url,type)
            mangaList.postValue(mangas)
        }
    }
}
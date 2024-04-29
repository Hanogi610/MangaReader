package fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.Manga
import scraper.AsuraJsoup

class SearchViewModel : ViewModel() {
    val searchResult = MutableLiveData<List<Manga>>()

    fun searchManga(query: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val scraper = AsuraJsoup()
                val mangas = scraper.getMangaByName(query)
                searchResult.postValue(mangas)
            }
        }
    }

}
package fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Genre
import model.HomepageManga
import scraper.NettruyenJsoup

class MangaTypeViewModel : ViewModel() {
    val genreList = MutableLiveData<List<Genre>>()
    val mangaList  = MutableLiveData<List<HomepageManga>>()
    val scraper = NettruyenJsoup()
    fun getGenre(){
        viewModelScope.launch(Dispatchers.IO){
            val genres = scraper.getMangaGenres()
            genreList.postValue(genres)
        }
    }
    fun fetchManga(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val mangas = scraper.getLatestMangas(url)
            mangaList.postValue(mangas)
        }
    }
}
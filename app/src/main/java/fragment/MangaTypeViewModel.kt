package fragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Genre
import model.Manga
import scraper.NettruyenJsoup
import kotlin.coroutines.cancellation.CancellationException

class MangaTypeViewModel : ViewModel() {
    val genreList = MutableLiveData<List<Genre>>()
    val mangaList  = MutableLiveData<List<Manga>>()
    val scraper = NettruyenJsoup()
    fun getGenre(){
        val job = viewModelScope.launch(Dispatchers.IO){
            val genres = scraper.getMangaGenres()
            genreList.postValue(genres)
        }
        job.invokeOnCompletion { cause ->
            if (cause is CancellationException) {
                Log.d("MangaTypeViewModel", "Coroutine was cancelled")
            }
        }
    }
    fun fetchManga(url: String) {
        val job = viewModelScope.launch(Dispatchers.IO) {
            val mangas = scraper.getLatestMangas(url)
            mangaList.postValue(mangas)
        }
        job.invokeOnCompletion { cause ->
            if (cause is CancellationException) {
                Log.d("MangaTypeViewModel", "Coroutine was cancelled")
            }
        }
    }
}
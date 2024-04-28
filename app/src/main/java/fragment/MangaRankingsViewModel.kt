package fragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Manga
import scraper.NettruyenJsoup
import kotlin.coroutines.cancellation.CancellationException

class MangaRankingsViewModel : ViewModel() {
    val mangaList = MutableLiveData<List<Manga>>()

    fun fetchManga(url: String) {
        val job = viewModelScope.launch(Dispatchers.IO) {
            val scraper = NettruyenJsoup()
            val mangas = scraper.getLatestMangas(url)
            mangaList.postValue(mangas)
        }
        job.invokeOnCompletion { cause ->
            if (cause is CancellationException) {
                Log.d("MangaRankingViewModel", "Coroutine was cancelled")
            }
        }
    }
}
package scraper

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.Chapter
import model.Manga
import model.MangaDetail
import org.jsoup.Jsoup

class MangaParkJsoup {
    suspend fun getLatestManga(url : String ) : List<Manga> = withContext(Dispatchers.IO){
        val mangas = mutableListOf<Manga>()
        val doc = Jsoup.connect(url).get()
        val element = doc.select("div.grid div.pb-3")
        for(e in element){
            val imageUrl = e.select("div.group a img").attr("src")
            val title = e.select("div.pl-3 h3 a span").text()
            val mangaUrl = "https://mangapark.io" + e.select("div.pl-3 h3 a").attr("href")
            val lastChapter = e.select("div[q:key=R7_8] span.grow a span").text()
            val manga = Manga(
                title,
                lastChapter,
                imageUrl,
                mangaUrl
            )
            Log.d("MangaParkJsoup", "getLatestManga() : ${manga.url} | ${manga.title} | ${manga.latestChapter} | ${manga.imageUrl}")
            mangas.add(manga)
        }
        return@withContext mangas
    }

    suspend fun getMangaDetail(url : String) : MangaDetail = withContext(Dispatchers.IO){
        val doc = Jsoup.connect(url).get()
        val title = doc.select("h3.text-lg")[0].select("a").text()
        val imageUrl = doc.select("div[q:key=17_2] img").attr("src")
        val authors = doc.select("div[q:key=tz_4] a")
        var author = ""
        for(a in authors){
            author += a.text() + ", "
        }
        val status = doc.select("span[q:key=Yn_5]").text()
        val genres = doc.select("div[q:key=30_2] span")
        var genre = ""
        for(g in genres){
            genre += g.select("span[q:key=kd_0]").text() + ", "
        }
        val description = doc.select("div[q:key=0Z_1] div.limit-html-p").text()
        val chapters = ArrayList<Chapter>()
        val elements = doc.select("div[q:key=8t_8]")
        val chapterCount = elements.size
        for(e in elements){
            val chapter = Chapter()
            chapter.chapterTitle = e.select("div.space-x-1 a").text()
            chapter.chapterUrl = "https://mangapark.io" + e.select("div a").attr("href")
            chapter.chapterDate = e.select("time span").text()
            chapters.add(chapter)
        }
        val mangaDetail = MangaDetail(
            title,
            url,
            imageUrl,
            author,
            status,
            genre,
            description,
            "",
            "",
            "",
            chapterCount.toString(),
            chapters,
            ""
        )
        Log.d("MangaParkJsoup", "getMangaDetail() : ${mangaDetail.mangaTitle} | ${mangaDetail.mangaUrl} | ${mangaDetail.mangaCover} | ${mangaDetail.mangaAuthor} | ${mangaDetail.mangaStatus} | ${mangaDetail.mangaGenre} | ${mangaDetail.mangaSynopsis} | ${mangaDetail.mangaChapterCount}")
        return@withContext mangaDetail
    }

    suspend fun getChapterImages(url : String) : List<String> = withContext(Dispatchers.IO){
        val doc = Jsoup.connect(url).get()
        Log.d("MangaParkJsoup", "getChapterImages() at ${url} : doc : $doc")
        val elements = doc.select("div[q:key=zn_2] div[data-name=image-item]")
        val images = mutableListOf<String>()
        for(element in elements){
            images.add(element.select("img").attr("src"))
            Log.d("MangaParkJsoup", "getChapterImages() : ${element.select("img").attr("src")}")
        }
        return@withContext images
    }

    suspend fun getChapterList(url : String) : List<Chapter> = withContext(Dispatchers.IO){
        val doc = Jsoup.connect(url).get()
        val elements = doc.select("div[q:key=8t_8]")
        val chapters = mutableListOf<Chapter>()
        for(e in elements){
            val chapter = Chapter()
            chapter.chapterTitle = e.select("div.space-x-1 a").text()
            chapter.chapterUrl = "https://mangapark.io" + e.select("div a").attr("href")
            chapter.chapterDate = e.select("time span").text()
            chapters.add(chapter)
        }
        return@withContext chapters
    }
}
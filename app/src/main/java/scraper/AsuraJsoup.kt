package scraper

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.Chapter
import model.Manga
import model.MangaByRank
import model.MangaDetail
import org.jsoup.Jsoup
import org.jsoup.select.Elements

class AsuraJsoup {
    suspend fun getLatestManga(url : String ) : List<Manga> = withContext(Dispatchers.IO){
        val mangas = mutableListOf<Manga>()
        val doc = Jsoup.connect(url).get()
        val element = doc.select("div.bixbox")[1].select("div.utao")
        for(e in element){
            val imageUrl = e.select("div.imgu img").attr("src")
            val title = e.select("div.luf a").attr("title")
            val mangaUrl = e.select("div.luf a").attr("href")
            val lastChapter = e.select("div.luf ul li")[0].text()
            val manga = Manga(
                title,
                lastChapter,
                imageUrl,
                mangaUrl
            )
            Log.d("AsuraJsoup", "getLatestManga() : ${manga.url} | ${manga.title} | ${manga.latestChapter} | ${manga.imageUrl}")
            mangas.add(manga)
        }
        return@withContext mangas
    }

    suspend fun getMangaDetail(url : String) : MangaDetail = withContext(Dispatchers.IO){
        val doc = Jsoup.connect(url).get()
        val infox = doc.select("div.bigcontent div.infox div.flex-wrap")
        val title = doc.select("div.bigcontent div.infox h1").text()
        val imageUrl = doc.select("div.bigcontent div.thumbook div img").attr("src")
        val author = infox[0].select("div")[1].select("span").text()
        val status = doc.select("div.bigcontent div.thumbook div.rt div.tsinfo i").text()
        val genres = doc.select("div.bigcontent div.infox div.wd-full")[1].select("span a")
        var genre = ""
        for(g in genres){
            genre += g.text() + ", "
        }
        val descriptions = doc.select("div.bigcontent div.infox div.wd-full")[0].select("div p")
        var description = ""
        for(d in descriptions){
            if(d.text().isNotEmpty()&&d.text()!="&nbsp;"){
                description += d.text()
            }
        }
        val chapters = ArrayList<Chapter>()
        val elements = doc.select("div.bxcl ul li a")
        val chapterCount = elements.size.toString() + " chapters"
        for(e in elements){
            val chapter = Chapter()
            chapter.chapterTitle = e.select("span.chapternum").text()
            chapter.chapterUrl = e.attr("href")
            chapter.chapterDate = e.select("span.chapterdate").text()
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
            chapters.reversed().toMutableList() as ArrayList<Chapter>,
            ""
        )
        Log.d("AsuraJsoup", "getMangaDetail() : ${mangaDetail.mangaTitle} | ${mangaDetail.mangaUrl} | ${mangaDetail.mangaCover} | ${mangaDetail.mangaAuthor} | ${mangaDetail.mangaStatus} | ${mangaDetail.mangaGenre} | ${mangaDetail.mangaSynopsis} | ${mangaDetail.mangaChapterCount}")
        return@withContext mangaDetail
    }

    suspend fun getChapterImages(url : String) : List<String> = withContext(Dispatchers.IO){
        val doc = Jsoup.connect(url).get()
        val elements = doc.select("div.maincontent div.rdminimal img")
        Log.d("AsuraJsoup", "getChapterImages() : ${elements.size}")
        val images = mutableListOf<String>()
        for(element in elements){
            images.add(element.attr("src"))
            Log.d("AsuraJsoup", "getChapterImages() : ${element.attr("src")}")
        }
        return@withContext images
    }

    suspend fun getChapterList(url : String) : List<Chapter> = withContext(Dispatchers.IO){
        val doc = Jsoup.connect(url).get()
        val elements = doc.select("div.bxcl ul li a")
        val chapters = mutableListOf<Chapter>()
        for(e in elements){
            val chapter = Chapter()
            chapter.chapterTitle = e.select("span.chapternum").text()
            chapter.chapterUrl = e.attr("href")
            chapter.chapterDate = e.select("span.chapterdate").text()
            Log.d("AsuraJsoup", "getChapterList() : ${chapter.chapterTitle} | ${chapter.chapterUrl} | ${chapter.chapterDate}")
            chapters.add(chapter)
        }
        return@withContext chapters.reversed()
    }

    suspend fun getMangaByRankingType(url: String, type: Int): List<MangaByRank> = withContext(Dispatchers.IO) {
        val doc = Jsoup.connect(url).get()
        val elements = when (type) {
            1 -> doc.select("div.wpop-weekly ul li")
            2 -> doc.select("div.wpop-monthly ul li")
            else -> doc.select("div.wpop-alltime ul li")
        }
        return@withContext getMangasFromElements(elements)
    }
    private fun getMangasFromElements(elements: Elements): List<MangaByRank> {
        val mangas = mutableListOf<MangaByRank>()
        for (e in elements) {
            val title = e.select("h2 a").text()
            val imageUrl = e.select("div.imgseries a img").attr("src")
            val mangaUrl = e.select("h2 a").attr("href")
            val genres = e.select("div.leftseries span a")
            var genre = ""
            for (g in genres) {
                if (g != genres[genres.size - 1]) {
                    genre += g.text() + ", "
                } else
                    genre += g.text()
            }
            val rating = e.select("div.leftseries div.rt div.numscore").text().toFloat() / 2
            val rank = e.select("div.ctr").text()
            val manga = MangaByRank(
                title,
                imageUrl,
                mangaUrl,
                genre,
                rating,
                rank
            )
            mangas.add(manga)
        }
        return mangas
    }

    suspend fun getMangaByName(url: String): List<Manga> = withContext(Dispatchers.IO){
        val mangas = mutableListOf<Manga>()
        url.replace(" ","+")
        val doc = Jsoup.connect("https://asuratoon.com/?s="+url).get()
        val pages = doc.select("div.pagination a")
        var lastPage : Int
        if(pages!=null&&pages.size>0) {
            lastPage = pages[pages.size - 2].text().toInt()
        }else{
            lastPage = 0
        }
        if(lastPage != 0){
            for(i in 1..lastPage){
                val pageUrl = "https://asuratoon.com/page/$i/?s=$url"
                val manga = getMangaByNamePerPage(pageUrl)
                mangas.addAll(manga)
            }
        }else{
            val pageUrl = "https://asuratoon.com/?s=$url"
            val manga = getMangaByNamePerPage(pageUrl)
            mangas.addAll(manga)
        }
        return@withContext mangas
    }

    suspend fun getMangaByNamePerPage(url: String): List<Manga> = withContext(Dispatchers.IO){
        val mangas = mutableListOf<Manga>()
        val doc = Jsoup.connect(url).get()
        val element = doc.select("div.bixbox div.listupd div.bs")
        for(e in element){
            val imageUrl = e.select("div.bsx a div.limit img").attr("src")
            val title = e.select("div.bsx a").attr("title")
            val mangaUrl = e.select("div.bsx a").attr("href")
            val lastChapter = e.select("div.epxs").text()
            val manga = Manga(
                title,
                lastChapter,
                imageUrl,
                mangaUrl
            )
            Log.d("AsuraJsoup", "getLatestManga() : ${manga.url} | ${manga.title} | ${manga.latestChapter} | ${manga.imageUrl}")
            mangas.add(manga)
        }
        return@withContext mangas
    }
}
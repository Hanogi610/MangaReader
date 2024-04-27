package scraper

import android.util.Log
import fragment.MangaTypeFragment
import model.Chapter
import model.Genre
import model.HomepageManga
import model.MangaDetail
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class NettruyenJsoup {

    suspend fun getDemo(): Any? {
       val url = "https://www.nettruyentt.com/"
        val doc = try {
            Jsoup.connect(url).userAgent("Mozilla/5.0").get()
        } catch (e: Exception) {
            Log.e("NettruyenJsoup", "Error connecting to $url", e)
        }
        return doc
    }
    suspend fun getLatestMangas(url : String) : List<HomepageManga> {
        println("NettruyenJsoup.getLatestMangas()")
        Log.d("NettruyenJsoup", "Connecting to $url")
        val doc = try {
            Jsoup.connect(url).userAgent("Mozilla/5.0").get()
        } catch (e: Exception) {

            Log.e("NettruyenJsoup", "Error connecting to $url", e)
            return emptyList()
        }
        Log.d("NettruyenJsoup", "Connected to $url")
        val elements = doc.select("div.container div.items div.item")
        println("elements size:" + elements.size)
        val mangas = mutableListOf<HomepageManga>()
        for(element in elements){
            val manga = HomepageManga()
            manga.title = element.select("h3 a").text()
            manga.latestChapter = element.select("ul.comic-item li")[0].select("a").text()
            manga.imageUrl ="https://"+element.select("div.image a img").attr("data-original")
            manga.url = element.select("h3 a").attr("href")
            manga.viewcount = element.select("div.view i.fa-eye").text()
            manga.commentcount = element.select("div.view i.fa-comment").text()
            manga.favcount = element.select("div.view i.fa-heart").text()
            println("manga title:" + manga.title)
            println("manga latest chap:" + manga.latestChapter)
            println("manga image url:" + manga.imageUrl)
            println("manga url:" + manga.url)
            mangas.add(manga)
            println("mangas size:" + mangas.size)
        }
        Log.d("NettruyenJsoup", "mangas.size = ${mangas.size}")
        return mangas
    }
    suspend fun getMangaGenres() : List<Genre> {
        println("NettruyenJsoup.getMangaGenres()")
        val url = "https://www.nettruyenvv.com/"
        val doc = try {
            Jsoup.connect(url).userAgent("Mozilla/5.0").get()
        } catch (e: Exception) {
            Log.e("NettruyenJsoup", "Error connecting to $url", e)
            return emptyList()
        }
        val elements = doc.select("li.dropdown")[0].select("ul.nav li")
        val genres = mutableListOf<Genre>()
        for(element in elements){
            val genre = Genre()
            genre.name = element.select("a").text()
            genre.url = element.select("a").attr("href")
            genres.add(genre)
        }
        println("genres size:" + genres.size)
        println("genres:" + genres[0].name + " | genres url:" + genres[0].url)
        return genres
    }

    suspend fun getMangaDetail(url : String) : MangaDetail{
        println("NettruyenJsoup.getMangaDetail()")
        val doc = try {
            Jsoup.connect(url).userAgent("Mozilla/5.0").get()
        } catch (e: Exception) {
            Log.e("NettruyenJsoup", "Error connecting to $url", e)
            return MangaDetail()
        }
        val manga = MangaDetail()
        manga.mangaTitle = doc.select("h1.title-detail").text()
        manga.mangaUrl = url
        manga.mangaCover = "https://" + doc.select("div.detail-info div.row div.col-image img").attr("src")
        val detailInfo = doc.select("div.detail-info div.col-info ul.list-info li")
        manga.mangaGenre = ""
        if(detailInfo.size == 5){
            manga.mangaOtherName = detailInfo[0].select("h2").text()
            manga.mangaAuthor = detailInfo[1].select("p.col-xs-8").text()
            manga.mangaStatus = detailInfo[2].select("p.col-xs-8").text()
            val genres = detailInfo[3].select("p.col-xs-8 a")
            for (genre in genres){
                manga.mangaGenre += genre.text() + ", "
            }
            manga.mangaViewCount = detailInfo[4].select("p.col-xs-8").text() +" views"
        }else{
            manga.mangaAuthor = detailInfo[0].select("p.col-xs-8").text()
            manga.mangaStatus = detailInfo[1].select("p.col-xs-8").text()
            val genres = detailInfo[2].select("p.col-xs-8 a")
            for (genre in genres){
                manga.mangaGenre += genre.text() + ", "
            }
            manga.mangaViewCount = detailInfo[3].select("p.col-xs-8").text() +" views"
        }
        manga.rating = doc.select("div.rating div.star .input").attr("value")
        manga.ratingCount = doc.select("div.mrt5 span.ratingCount").text()
        manga.mangaSynopsis = doc.select("div.detail-content p").text()
        val chapterElements = doc.select("div.list-chapter ul li")
        manga.mangaChapterCount = chapterElements.size.toString() + " chapters"
        manga.mangaChapterList = ArrayList()
        for (element in chapterElements){
            val chapter = Chapter()
            val chapterDetail = element.select("div")
            chapter.chapterTitle = chapterDetail[0].text()
            chapter.chapterUrl = chapterDetail[0].attr("href")
            chapter.chapterDate = chapterDetail[1].text()
            chapter.chapterView = chapterDetail[2].text()
            manga.mangaChapterList?.add(chapter)
        }
        println("manga first chapter:" + manga.mangaChapterList?.last()?.chapterTitle)
        return manga
    }

}
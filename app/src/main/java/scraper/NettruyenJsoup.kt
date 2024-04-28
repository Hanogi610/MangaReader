package scraper

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import model.Chapter
import model.Genre
import model.Manga
import model.MangaDetail
import org.jsoup.Jsoup

class NettruyenJsoup {

    suspend fun getLatestMangas(url : String) : List<Manga> = withContext(Dispatchers.IO){
        println("NettruyenJsoup.getLatestMangas()")
        Log.d("NettruyenJsoup", "Connecting to $url")
        val doc = try {
            Jsoup.connect(url).userAgent("Mozilla/5.0").get()
        } catch (e: Exception) {

            Log.e("NettruyenJsoup", "Error connecting to $url", e)
            return@withContext emptyList()
        }
        Log.d("NettruyenJsoup", "Connected to $url")
        val elements = doc.select("div.container div.items div.item")
        println("elements size:" + elements.size)
        val mangas = mutableListOf<Manga>()
        for(element in elements){
            val manga = Manga(
                title = "",
                latestChapter = "",
                imageUrl = "",
                url = "",
                viewcount = "",
                commentcount = "",
                favcount = ""
            )
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
        return@withContext mangas
    }
    suspend fun getMangaGenres() : List<Genre> = withContext(Dispatchers.IO){
        println("NettruyenJsoup.getMangaGenres()")
        val url = "https://www.nettruyenvv.com/"
        val doc = try {
            Jsoup.connect(url).userAgent("Mozilla/5.0").get()
        } catch (e: Exception) {
            Log.e("NettruyenJsoup", "Error connecting to $url", e)
            return@withContext emptyList()
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
        return@withContext genres
    }

    suspend fun getMangaDetail(url : String) : MangaDetail = withContext(Dispatchers.IO){
        try {
            withTimeout(10000){
                println("NettruyenJsoup.getMangaDetail()")
                val doc = try {
                    Jsoup.connect(url).userAgent("Mozilla/5.0").get()
                } catch (e: Exception) {
                    Log.e("NettruyenJsoup", "Error connecting to $url", e)
                    return@withTimeout MangaDetail(
                        mangaTitle = "",
                        mangaUrl = "",
                        mangaCover = "",
                        mangaOtherName = "",
                        mangaAuthor = "",
                        mangaStatus = "",
                        mangaGenre = "",
                        mangaViewCount = "",
                        rating = "",
                        ratingCount = "",
                        mangaSynopsis = "",
                        mangaChapterCount = "",
                        mangaChapterList = ArrayList()
                    )
                }
                val manga = MangaDetail(
                    mangaTitle = "",
                    mangaUrl = "",
                    mangaCover = "",
                    mangaOtherName = "",
                    mangaAuthor = "",
                    mangaStatus = "",
                    mangaGenre = "",
                    mangaViewCount = "",
                    rating = "",
                    ratingCount = "",
                    mangaSynopsis = "",
                    mangaChapterCount = "",
                    mangaChapterList = ArrayList()
                )
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
                val chaps = ArrayList<Chapter>()
                manga.mangaChapterList = ArrayList()
                for (element in chapterElements){
                    val chapter = Chapter()
                    val chapterDetail = element.select("div")
                    chapter.chapterTitle = chapterDetail[0].text()
                    chapter.chapterUrl = chapterDetail[0].select("a").attr("href")
                    chapter.chapterDate = chapterDetail[1].text()
                    chapter.chapterView = chapterDetail[2].text()
                    chaps.add(chapter)
                }
                manga.mangaChapterList = chaps.reversed().toMutableList() as ArrayList<Chapter>
                println("manga first chapter:" + manga.mangaChapterList?.first()?.chapterTitle + " " + manga.mangaChapterList?.first()?.chapterUrl)
                return@withTimeout manga
            }
        }catch (e: Exception){
            Log.e("NettruyenJsoup", "Error getting manga detail", e)
            return@withContext MangaDetail(
                mangaTitle = "",
                mangaUrl = "",
                mangaCover = "",
                mangaOtherName = "",
                mangaAuthor = "",
                mangaStatus = "",
                mangaGenre = "",
                mangaViewCount = "",
                rating = "",
                ratingCount = "",
                mangaSynopsis = "",
                mangaChapterCount = "",
                mangaChapterList = ArrayList()
            )
        }

    }

    suspend fun getChapterImages(url: String) : List<String> = withContext(Dispatchers.IO){
        println("NettruyenJsoup.getChapterImages()")
        val doc = try {
            Jsoup.connect(url).userAgent("Mozilla/5.0").get()
        } catch (e: Exception) {
            Log.e("NettruyenJsoup", "Error connecting to $url", e)
            return@withContext emptyList()
        }
        val elements = doc.select("div.reading-detail div")
        val images = mutableListOf<String>()
        for(element in elements){
            images.add("https:"+element.select("img").attr("src"))
        }
        println("images size:" + images.size)
        return@withContext images
    }

    suspend fun getChapterList(url: String) : List<Chapter> = withContext(Dispatchers.IO){
        println("NettruyenJsoup.getChapterList()")
        val doc = try {
            Jsoup.connect(url).userAgent("Mozilla/5.0").get()
        } catch (e: Exception) {
            Log.e("NettruyenJsoup", "Error connecting to $url", e)
            return@withContext emptyList()
        }
        val elements = doc.select("div.list-chapter ul li")
        val chapters = mutableListOf<Chapter>()
        for(element in elements){
            val chapter = Chapter()
            val chapterDetail = element.select("div")
            chapter.chapterTitle = chapterDetail[0].text()
            chapter.chapterUrl = chapterDetail[0].select("a").attr("href")
            chapter.chapterDate = chapterDetail[1].text()
            chapter.chapterView = chapterDetail[2].text()
            chapters.add(chapter)
        }
        println("chapters size:" + chapters.size)
        return@withContext chapters
    }

}
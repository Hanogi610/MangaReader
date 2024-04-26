package scraper

import android.util.Log
import model.HomepageManga
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
            manga.imageUrl =element.select("div.image a img").attr("data-original")
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


}
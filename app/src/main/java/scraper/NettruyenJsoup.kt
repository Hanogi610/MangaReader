package scraper

import org.jsoup.Jsoup

class NettruyenJsoup {
    suspend fun getHomepage() {
        val url = "https://nettruyentt.com/"
        val doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get()
        val elements = doc.select("div.container > div.items > div.item")
    }
}
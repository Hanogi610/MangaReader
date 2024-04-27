package model

import java.io.Serializable

class MangaDetail : Serializable {
    var mangaTitle : String? = null
    var mangaUrl : String? = null
    var mangaCover : String? = null
    var mangaAuthor : String? = null
    var mangaStatus : String? = null
    var mangaGenre : String? = null
    var mangaSynopsis : String? = null
    var mangaViewCount : String? = null
    var rating : String? = null
    var ratingCount : String? = null
    var mangaChapterCount : String? = null
    var mangaChapterList : ArrayList<Chapter>? = null
    var mangaOtherName : String? = null
}
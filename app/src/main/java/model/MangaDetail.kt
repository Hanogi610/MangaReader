package model

import java.io.Serializable

data class MangaDetail  (
    var mangaTitle : String,
    var mangaUrl : String,
    var mangaCover : String,
    var mangaAuthor : String,
    var mangaStatus : String,
    var mangaGenre : String,
    var mangaSynopsis : String,
    var mangaViewCount : String,
    var rating : String,
    var ratingCount : String,
    var mangaChapterCount : String,
    var mangaChapterList : ArrayList<Chapter>,
    var mangaOtherName : String,
    ) : Serializable
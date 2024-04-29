package model


import java.io.Serializable

data class Manga  (
    var title: String,
    var latestChapter: String,
    var imageUrl: String,
    var url: String,
) : Serializable



package model

import dbModel.FavoriteManga
import java.io.Serializable
import java.util.Date

data class Manga  (
    var title: String,
    var latestChapter: String,
    var imageUrl: String,
    var url: String,
    var viewcount: String,
    var commentcount: String,
    var favcount: String,
) : Serializable



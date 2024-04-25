package model

import java.io.Serializable

class HomepageManga : Serializable {
    var title: String? = null
    var latestChapter: String? = null
    var imageUrl: String? = null
    var url: String? = null
    var viewcount: String? = null
    var commentcount: String? = null
    var favcount: String? = null
}
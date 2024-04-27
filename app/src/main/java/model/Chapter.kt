package model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

class Chapter : Serializable {
    var chapterTitle : String? = null
    var chapterUrl : String? = null
    var chapterDate : String? = null
    var chapterView : String? = null
}
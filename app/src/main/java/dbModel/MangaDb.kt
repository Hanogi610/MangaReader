package dbModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manga")
data class MangaDb(
    @PrimaryKey(autoGenerate = true)
    var id : Long,
    val mangaTitle: String,
    val mangaUrl: String,
    val mangaSynopsis: String,
    val mangaStatus: String,
    val mangaAuthor: String,
    val mangaGenre: String,
    val mangaView: String,
    val mangaRate: String,
    var isHistory : Boolean,
    var isFavorite : Boolean,
    var lastRead : String,
    var lastReadChapterPost : Int
)

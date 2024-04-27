package dbModel

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "chapter",
    foreignKeys = [
        ForeignKey(
            entity = MangaDb::class,
            parentColumns = ["id"],
            childColumns = ["mangaId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ChapterInDb(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val chapterTitle: String,
    val chapterUrl: String,
    val chapterDate: String,
    val chapterView: String,
    val isRead: Boolean = false,
    val mangaId: Long,
    val chapterPost : Int
)

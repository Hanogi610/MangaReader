package com.example.mangareader.activity

import adapter.MangaDetailRvAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mangareader.R
import database.AppDatabase
import dbModel.ChapterInDb
import dbModel.MangaDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MangaDetailActivity : AppCompatActivity() {
    private lateinit var mangaCover: ImageView
    lateinit var mangaTitle: TextView
    lateinit var mangaAuthor: TextView
    lateinit var mangaStatus: TextView
    lateinit var mangaGenre: TextView
    lateinit var mangaSynopsis: TextView
    lateinit var mangaViewCount: TextView
    lateinit var mangaChapterCount: TextView
    lateinit var chapterList: RecyclerView
    lateinit var bg : ImageView
    lateinit var favoriteButton : Button
    lateinit var readNowButton: Button
    lateinit var checkButton: Button
    lateinit var INSTANCE : AppDatabase

    private val viewModel : MangaDetailViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manga_detail)

        mangaCover = findViewById(R.id.manga_cover)
        mangaTitle = findViewById(R.id.manga_title)
        mangaAuthor = findViewById(R.id.manga_author)
        mangaStatus = findViewById(R.id.manga_status)
        mangaGenre = findViewById(R.id.manga_genre)
        mangaSynopsis = findViewById(R.id.manga_synopsis)
        mangaViewCount = findViewById(R.id.manga_view_count)
        mangaChapterCount = findViewById(R.id.chapter_count)
        chapterList = findViewById(R.id.chapter_list)
        favoriteButton = findViewById(R.id.manga_favorite)
        readNowButton = findViewById(R.id.manga_read)
        checkButton = findViewById(R.id.manga_check_for_updates)
        INSTANCE = AppDatabase.getInstance(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        bg = toolbar.findViewById<ImageView>(R.id.manga_cover_to_bg)
        setSupportActionBar(toolbar)
        val backButton = toolbar.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        val url = intent.getStringExtra("url")
        viewModel.getMangaDetail(url!!)

        // Observe the mangaDetail LiveData
        viewModel.mangaDetail.observe(this, Observer{ mangaDetail ->
            // Set the data to the views
            Glide.with(this@MangaDetailActivity).load(mangaDetail.mangaCover).into(mangaCover)
            Glide.with(this@MangaDetailActivity).load(mangaDetail.mangaCover).into(bg)
            mangaTitle.text = mangaDetail.mangaTitle
            mangaAuthor.text = mangaDetail.mangaAuthor
            mangaStatus.text = mangaDetail.mangaStatus
            mangaGenre.text = mangaDetail.mangaGenre
            mangaViewCount.text = mangaDetail.mangaViewCount
            mangaChapterCount.text = mangaDetail.mangaChapterCount
            val adapter = MangaDetailRvAdapter(mangaDetail.mangaChapterList!!)
            chapterList.layoutManager = LinearLayoutManager(this@MangaDetailActivity)
            chapterList.adapter = adapter

            // Set the synopsis with a max line default of 3 and can be extended when clicked
            var isExpanded = false
            mangaSynopsis.text = mangaDetail.mangaSynopsis + " ...More"
            mangaSynopsis.maxLines = 3
            mangaSynopsis.setOnClickListener {
                it as TextView
                if (isExpanded) {
                    // If the synopsis is currently expanded, collapse it
                    it.text = mangaDetail.mangaSynopsis + " ...Less"
                    it.maxLines = 3
                    isExpanded = false
                } else {
                    // If the synopsis is currently collapsed, expand it
                    it.text = mangaDetail.mangaSynopsis
                    it.maxLines = Integer.MAX_VALUE
                    isExpanded = true
                }
            }
        })

        // Set the onClickListener for the favorite button
        favoriteButton.setOnClickListener {
            // Retrieve mangaDetail value from LiveData safely
            viewModel.mangaDetail.value?.let { mangaDetail ->
                val mangaDb = MangaDb(
                    0,
                    mangaDetail.mangaTitle!!,
                    mangaDetail.mangaUrl!!,
                    mangaDetail.mangaSynopsis!!,
                    mangaDetail.mangaStatus!!,
                    mangaDetail.mangaAuthor!!,
                    mangaDetail.mangaGenre!!,
                    mangaDetail.mangaViewCount!!,
                    mangaDetail.rating!!,
                    false,
                    true,
                    "",
                    0
                )
                // Perform database operation in a coroutine
                viewModel.viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        mangaDb.id = INSTANCE.mangaDbDAO().insertManga(mangaDb)

                        // Iterate over the mangaChapterList and set chapterPost
                        for ((index, chapter) in mangaDetail.mangaChapterList!!.withIndex()) {
                            val chapterDb = ChapterInDb(
                                0L, // Convert 0 to Long
                                chapter.chapterTitle ?: "", // Provide default value when chapterTitle is null
                                chapter.chapterUrl ?: "",
                                chapter.chapterDate ?: "",
                                chapter.chapterView ?: "",
                                false,
                                mangaDb.id,
                                index
                            )
                            INSTANCE.chapterInDbDAO().insertChapter(chapterDb)
                        }
                    }
                }

            }
        }

        // Set the onClickListener for the read now button
        readNowButton.setOnClickListener {
            viewModel.mangaDetail.value?.let { mangaDetail ->
                val manga = INSTANCE.mangaDbDAO().checkMangaIfExist(mangaDetail.mangaUrl!!)
                if(manga != null && manga.isHistory){
                    val chapterList = INSTANCE.chapterInDbDAO().getChapterByMangaId(manga.id)
                    chapterList.sortedBy { it.chapterPost }
                    for(chapter in chapterList){
                        if(chapter.isRead){
                            val time = System.currentTimeMillis()
                            manga.lastRead = time.toString()
                            INSTANCE.mangaDbDAO().updateLastRead(time.toString(), manga.id)
                            val intent = Intent(this, ChapContentActivity::class.java)
                            intent.putExtra("chapterUrl", chapter.chapterUrl)
                            //startActivity(intent)
                            break
                        }
                    }
                }else if(manga != null && !manga.isHistory){
                    val chapterList = INSTANCE.chapterInDbDAO().getChapterByMangaId(manga.id)
                    chapterList.sortedBy { it.chapterPost }
                    val time = System.currentTimeMillis()
                    manga.lastRead = time.toString()
                    INSTANCE.mangaDbDAO().updateLastRead(time.toString(), manga.id)
                    INSTANCE.mangaDbDAO().updateIsHistory(true, manga.id)
                    INSTANCE.chapterInDbDAO().updateIsRead(true, chapterList[0].id)
                    val intent = Intent(this, ChapContentActivity::class.java)
                    intent.putExtra("chapterUrl", chapterList[0].chapterUrl)
                    //startActivity(intent)
                }else if(manga == null){
                    viewModel.viewModelScope.launch {
                        withContext(Dispatchers.IO){
                            val mangaDb = MangaDb(
                                0,
                                mangaDetail.mangaTitle!!,
                                mangaDetail.mangaUrl!!,
                                mangaDetail.mangaSynopsis!!,
                                mangaDetail.mangaStatus!!,
                                mangaDetail.mangaAuthor!!,
                                mangaDetail.mangaGenre!!,
                                mangaDetail.mangaViewCount!!,
                                mangaDetail.rating!!,
                                true,
                                false,
                                "",
                                0
                            )
                            mangaDb.id = INSTANCE.mangaDbDAO().insertManga(mangaDb)
                            for ((index, chapter) in mangaDetail.mangaChapterList!!.withIndex()) {
                                val chapterDb = ChapterInDb(
                                    0L,
                                    chapter.chapterTitle ?: "",
                                    chapter.chapterUrl ?: "",
                                    chapter.chapterDate ?: "",
                                    chapter.chapterView ?: "",
                                    false,
                                    mangaDb.id,
                                    index
                                )
                                INSTANCE.chapterInDbDAO().insertChapter(chapterDb)
                            }
                            val chapterList = INSTANCE.chapterInDbDAO().getChapterByMangaId(mangaDb.id)
                            chapterList.sortedBy { it.chapterPost }
                            val time = System.currentTimeMillis()
                            mangaDb.lastRead = time.toString()
                            INSTANCE.mangaDbDAO().updateLastRead(time.toString(), mangaDb.id)
                            INSTANCE.mangaDbDAO().updateIsHistory(true, mangaDb.id)
                            INSTANCE.chapterInDbDAO().updateIsRead(true, chapterList[0].id)
                            val intent = Intent(this@MangaDetailActivity, ChapContentActivity::class.java)
                            intent.putExtra("chapterUrl", chapterList[0].chapterUrl)
                            //startActivity(intent)
                        }
                    }
                }
            }
        }
    }

}
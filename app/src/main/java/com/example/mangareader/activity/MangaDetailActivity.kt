package com.example.mangareader.activity

import adapter.MangaDetailRvAdapter
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mangareader.R

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
    }
}
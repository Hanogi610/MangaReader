package com.example.mangareader.activity

import com.example.mangareader.adapter.MangaDetailRvAdapter
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mangareader.R
import com.example.mangareader.database.AppDatabase
import com.example.mangareader.dbModel.FavoriteManga
import com.example.mangareader.dbModel.HistoryManga
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class MangaDetailActivity : AppCompatActivity() {
    private lateinit var mangaCover: ImageView
    lateinit var mangaTitle: TextView
    lateinit var mangaAuthor: TextView
    lateinit var mangaStatus: TextView
    lateinit var mangaGenre: TextView
    lateinit var mangaSynopsis: TextView
    lateinit var mangaViewCount: TextView
    lateinit var mangaChapterCount: TextView
    lateinit var chapterListRv: RecyclerView
    lateinit var bg : ImageView
    lateinit var favoriteButton : Button
    lateinit var readNowButton: Button
    lateinit var checkButton: Button
    lateinit var INSTANCE : AppDatabase
    lateinit var mangaInFav : FavoriteManga
    lateinit var mangaInHistory : HistoryManga
    lateinit var adapter: MangaDetailRvAdapter
    lateinit var tvNoInternet : TextView
    lateinit var toolbar: Toolbar
    lateinit var toolbarView : View
    var isExistInFav = 0
    var isExistInHis = 0
    var url : String = ""


    private val viewModel : MangaDetailViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manga_detail)

        initialize()

        checkMangaIfIsExistInFav(url)
        checkMangaIfIsExistInHistory(url)

        if(!isNetworkAvailable(this)){
            tvNoInternet.visibility = View.VISIBLE
            toolbarView.visibility = View.GONE
        }else{
            tvNoInternet.visibility = View.GONE
            toolbarView.visibility = View.VISIBLE
            viewModel.getMangaDetail(url)
        }

        tvNoInternet.setOnClickListener(View.OnClickListener {
            if(isNetworkAvailable(this)){
                tvNoInternet.visibility = View.GONE
                toolbarView.visibility = View.VISIBLE
                viewModel.getMangaDetail(url)
            }else{
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        })

        // Observe the mangaDetail LiveData
        viewModel.mangaDetail.observe(this, Observer{ mangaDetail ->
            // Set the data to the views
            Glide.with(this@MangaDetailActivity).load(intent.getStringExtra("img")).into(mangaCover)
            Glide.with(this@MangaDetailActivity).load(mangaDetail.mangaCover).into(bg)
            mangaTitle.text = mangaDetail.mangaTitle
            mangaAuthor.text = mangaDetail.mangaAuthor
            mangaStatus.text = mangaDetail.mangaStatus
            mangaGenre.text = mangaDetail.mangaGenre
            mangaViewCount.text = mangaDetail.mangaViewCount
            mangaChapterCount.text = mangaDetail.mangaChapterCount
            adapter = MangaDetailRvAdapter(mangaDetail.mangaChapterList){ chapter, position ->
                val intent = Intent(this@MangaDetailActivity, ChapContentActivity::class.java)
                intent.putExtra("chapterPost", position)
                intent.putExtra("mangaUrl", url)
                intent.putExtra("chapterList",mangaDetail.mangaChapterList)
                intent.putExtra("chapUrl", chapter.chapterUrl)
                intent.putExtra("chapTitle", chapter.chapterTitle)
                viewModel.viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        if(isExistInHis==0){
                            mangaInHistory = HistoryManga(
                                0,
                                mangaDetail.mangaTitle,
                                mangaDetail.mangaUrl,
                                mangaDetail.mangaCover,
                                chapter.chapterTitle.toString(),
                                chapter.chapterUrl.toString(),
                                Date().time,
                                0
                            )
                            mangaInHistory.id = INSTANCE.historyMangaDAO().insertHistoryManga(mangaInHistory)
                        }else{
                            mangaInHistory.lastReadChapterName = chapter.chapterTitle.toString()
                            mangaInHistory.lastReadChapterUrl = chapter.chapterUrl.toString()
                            mangaInHistory.lastRead = Date().time
                            mangaInHistory.lastReadChapterPost = position
                            INSTANCE.historyMangaDAO().updateHistoryManga(mangaInHistory.mangaUrl, mangaInHistory.lastReadChapterName,mangaInHistory.lastReadChapterUrl, mangaInHistory.lastRead, mangaInHistory.lastReadChapterPost)
                        }
                    }
                }
                startActivity(intent)
            }
            chapterListRv.layoutManager = LinearLayoutManager(this@MangaDetailActivity)
            chapterListRv.adapter = adapter

            // Set the synopsis with a max line default of 3 and can be extended when clicked
            var isExpanded = false
            mangaSynopsis.text = getString(R.string.manga_synopsis_more, mangaDetail.mangaSynopsis)
            mangaSynopsis.maxLines = 3
            mangaSynopsis.setOnClickListener {
                it as TextView
                if (isExpanded) {
                    // If the synopsis is currently expanded, collapse it
                    it.text = getString(R.string.manga_synopsis_less, mangaDetail.mangaSynopsis)
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

        viewModel.chapterList.observe(this, Observer { chapterList ->
            adapter.updateData(chapterList, this@MangaDetailActivity)
            mangaChapterCount.text = chapterList.size.toString() + " chapters"
        })

        // Set the onClickListener for the favorite button
        favoriteButton.setOnClickListener {
            // Retrieve mangaDetail value from LiveData safely
            if(isExistInFav == 1){
                viewModel.viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        INSTANCE.favoriteMangaDAO().deleteFavoriteManga(mangaInFav)
                    }
                    isExistInFav = 0
                    upDateFavoriteButton(isExistInFav)
                }
            }else{
                viewModel.mangaDetail.value?.let { mangaDetail ->
                    mangaInFav = FavoriteManga(0, mangaDetail.mangaTitle!!, mangaDetail.mangaUrl!!, mangaDetail.mangaCover!!, Date().time)
                    // Perform database operation in a coroutine
                    viewModel.viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            mangaInFav.id = INSTANCE.favoriteMangaDAO().insertFavoriteManga(mangaInFav)
                        }
                        isExistInFav = 1
                        upDateFavoriteButton(isExistInFav)
                    }

                }
            }
        }

        // Set the onClickListener for the read now button
        readNowButton.setOnClickListener {
            viewModel.mangaDetail.value?.let { mangaDetail ->
                // Perform database operation in a coroutine
                viewModel.viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        if(isExistInHis==0){
                            mangaInHistory = HistoryManga(
                                0,
                                mangaDetail.mangaTitle,
                                mangaDetail.mangaUrl,
                                mangaDetail.mangaCover,
                                mangaDetail.mangaChapterList.first().chapterTitle.toString(),
                                mangaDetail.mangaChapterList.first().chapterUrl.toString(),
                                Date().time,
                                0
                            )
                            mangaInHistory.id = INSTANCE.historyMangaDAO().insertHistoryManga(mangaInHistory)
                            val intentC = Intent(this@MangaDetailActivity, ChapContentActivity::class.java)
                            intentC.putExtra("chapterPost", mangaInHistory.lastReadChapterPost)
                            intentC.putExtra("mangaUrl", mangaInHistory.mangaUrl)
                            intentC.putExtra("chapterList",mangaDetail.mangaChapterList)
                            intentC.putExtra("chapUrl", mangaInHistory.lastReadChapterUrl)
                            intentC.putExtra("chapTitle", mangaInHistory.lastReadChapterName)
                            startActivity(intentC)
                        }else{
                            mangaInHistory.lastRead = Date().time
                            val intentC = Intent(this@MangaDetailActivity, ChapContentActivity::class.java)
                            intentC.putExtra("chapterPost", mangaInHistory.lastReadChapterPost)
                            intentC.putExtra("mangaUrl", mangaInHistory.mangaUrl)
                            intentC.putExtra("chapterList",mangaDetail.mangaChapterList)
                            intentC.putExtra("chapUrl", mangaInHistory.lastReadChapterUrl)
                            intentC.putExtra("chapTitle", mangaInHistory.lastReadChapterName)
                            INSTANCE.historyMangaDAO().updateHistoryMangaLastReadById(mangaInHistory.id, mangaInHistory.lastRead)
                            startActivity(intentC)
                        }
                    }
                }
            }
        }

        checkButton.setOnClickListener{
            viewModel.getChapterList(url)
        }
    }

    private fun initialize() {
        // Initialize the views
        mangaCover = findViewById(R.id.manga_cover)
        mangaTitle = findViewById(R.id.manga_title)
        mangaAuthor = findViewById(R.id.manga_author)
        mangaStatus = findViewById(R.id.manga_status)
        mangaGenre = findViewById(R.id.manga_genre)
        mangaSynopsis = findViewById(R.id.manga_synopsis)
        mangaViewCount = findViewById(R.id.manga_view_count)
        mangaChapterCount = findViewById(R.id.chapter_count)
        chapterListRv = findViewById(R.id.chapter_list)
        favoriteButton = findViewById(R.id.manga_favorite)
        readNowButton = findViewById(R.id.manga_read)
        checkButton = findViewById(R.id.manga_check_for_updates)
        tvNoInternet = findViewById(R.id.tv_no_internet)

        //init db
        INSTANCE = AppDatabase.getInstance(this)
        url = intent.getStringExtra("url").toString()

        // Set up the toolbar
        toolbar = findViewById(R.id.toolbar)
        toolbarView = findViewById(R.id.bg_fade_toolbar_view)
        bg = toolbar.findViewById<ImageView>(R.id.manga_cover_to_bg)
        setSupportActionBar(toolbar)
        val backButton = toolbar.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun upDateFavoriteButton(exist : Int){
        if(exist==1){
            favoriteButton.text = "Unfavorite"
        }else{
            favoriteButton.text = "Favorite"
        }
    }

    private fun checkMangaIfIsExistInFav(url : String){
        // Perform database operation in a coroutine
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val favMangas = INSTANCE.favoriteMangaDAO().getAllFavoriteManga()
                for(manga in favMangas){
                    if(manga.mangaUrl == url){
                        isExistInFav = 1
                        mangaInFav = manga
                        break
                    }
                }
            }
            upDateFavoriteButton(isExistInFav)
        }
    }

    private fun checkMangaIfIsExistInHistory(url : String){
        // Perform database operation in a coroutine
        Log.d("MangaDetailActivity", "checkMangaIfIsExistInHistory: $url")
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val historyManga = INSTANCE.historyMangaDAO().getAllHistoryManga()
                for(manga in historyManga){
                    if(manga.mangaUrl == url){
                        isExistInHis = 1
                        mangaInHistory = manga
                        break
                    }
                }
            }
        }
    }
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
package com.example.mangareader.activity

import adapter.ChapterContentRvAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import com.google.android.material.bottomsheet.BottomSheetDragHandleView
import database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Chapter
import java.util.Date

class ChapContentActivity : AppCompatActivity() {

    lateinit var mangaUrl : String
    lateinit var chapUrl : String
    lateinit var bottomSheet : BottomSheetDragHandleView
    lateinit var imageRv : RecyclerView
    lateinit var nextButton: ImageButton
    lateinit var prevButton: ImageButton
    lateinit var chapterList : ArrayList<Chapter>
    lateinit var chapterContentRvAdapter: ChapterContentRvAdapter
    lateinit var chapterImages :List<String>
    lateinit var INSTANCE : AppDatabase
    var chapPost = 0
    private val viewModel : ChapContentViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chap_content)

        initialize()

        if(chapterList.isEmpty()){
            getChapterList(mangaUrl)
            viewModel.chapterList.observe(this, Observer{
                chapterList = it as ArrayList<Chapter>
            })
            chapUrl = chapterList[chapPost].chapterUrl.toString()
        }else{
            chapUrl = chapterList[chapPost].chapterUrl.toString()
        }

        chapterImages = ArrayList<String>()

        getChapterImages(chapUrl)

        viewModel.chapContent.observe(this, Observer{
            chapterImages = it
            chapterContentRvAdapter = ChapterContentRvAdapter(chapterImages)
            imageRv.adapter = chapterContentRvAdapter
            imageRv.layoutManager = LinearLayoutManager(this)
        })


        nextButton.setOnClickListener {
            if(chapPost < chapterList.size){
                chapPost++
                val intentC = Intent(this, ChapContentActivity::class.java)
                intentC.putExtra("mangaUrl", mangaUrl)
                intentC.putExtra("chapterList", chapterList)
                intentC.putExtra("chapPost", chapPost)
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    INSTANCE.historyMangaDAO().updateHistoryManga(mangaUrl, chapterList.get(chapPost).chapterTitle.toString(), chapterList.get(chapPost).chapterUrl.toString(),Date().time,chapPost)
                }
                startActivity(intentC)
            }else{
                Toast.makeText(this, "This is the newest chapter", Toast.LENGTH_SHORT).show()
            }
        }
        prevButton.setOnClickListener {
            if(chapPost > 0){
                chapPost--
                val intentC = Intent(this, ChapContentActivity::class.java)
                intentC.putExtra("mangaUrl", mangaUrl)
                intentC.putExtra("chapterList", chapterList)
                intentC.putExtra("chapPost", chapPost)
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    INSTANCE.historyMangaDAO().updateHistoryManga(mangaUrl, chapterList.get(chapPost).chapterTitle.toString(), chapterList.get(chapPost).chapterUrl.toString(),Date().time,chapPost)
                }
                startActivity(intentC)
            }else{
                Toast.makeText(this, "This is the first chapter", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initialize() {
        mangaUrl = intent.getStringExtra("mangaUrl").toString()
        chapterList = intent.getSerializableExtra("chapterList") as ArrayList<Chapter>
        chapPost = intent.getIntExtra("chapPost", 0)

        //val toolBar = findViewById(R.id.toolbar)
        bottomSheet = findViewById(R.id.chapter_list)
        imageRv = findViewById(R.id.chapterContentRv)
        nextButton = findViewById(R.id.nextChapterButton)
        prevButton = findViewById(R.id.prevChapterButton)
        INSTANCE = AppDatabase.getInstance(this)
    }

    private fun getChapterList(url : String){
        viewModel.getChapterList(url)
    }
    private fun getChapterImages(url : String){
        viewModel.getChapterImages(url)
    }
}
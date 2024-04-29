package com.example.mangareader.activity

import adapter.BottomSheetRvAdapter
import adapter.ChapterContentRvAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDragHandleView
import database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Chapter
import java.util.Date

class ChapContentActivity : AppCompatActivity() {

    lateinit var mangaUrl : String
    lateinit var chapUrl : String
    lateinit var backButton : ImageButton
    lateinit var bottomSheet : BottomSheetDragHandleView
    lateinit var imageRv : RecyclerView
    lateinit var nextButton: ImageButton
    lateinit var prevButton: ImageButton
    lateinit var chapterList : ArrayList<Chapter>
    lateinit var chapterContentRvAdapter: ChapterContentRvAdapter
    lateinit var chapterImages :List<String>
    lateinit var INSTANCE : AppDatabase
    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var bottomSheetRvAdapter: BottomSheetRvAdapter
    var chapPost = 0
    private val viewModel : ChapContentViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chap_content)

        initialize()

        chapterListInit()

        bottomSheetInit()

        chapterImages = ArrayList<String>()

        getChapterImages(chapUrl)

        viewModel.chapContent.observe(this, Observer{
            chapterImages = it
            chapterContentRvAdapter = ChapterContentRvAdapter(chapterImages)
            imageRv.adapter = chapterContentRvAdapter
            imageRv.layoutManager = LinearLayoutManager(this)
        })

        if(chapPost == 0){
            prevButton.isEnabled = false
        }
        if(chapPost == chapterList.size){
            nextButton.isEnabled = false
        }

        nextButton.setOnClickListener {
            if(chapPost < chapterList.size){
                chapPost++
                val intentC = Intent(this, ChapContentActivity::class.java)
                intentC.putExtra("mangaUrl", mangaUrl)
                intentC.putExtra("chapterList", chapterList)
                intentC.putExtra("chapPost", chapPost)
                intentC.putExtra("chapUrl",chapterList.get(chapPost).chapterUrl)
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
                intentC.putExtra("chapUrl",chapterList.get(chapPost).chapterUrl)
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    INSTANCE.historyMangaDAO().updateHistoryManga(mangaUrl, chapterList.get(chapPost).chapterTitle.toString(), chapterList.get(chapPost).chapterUrl.toString(),Date().time,chapPost)
                }
                startActivity(intentC)
            }else{
                Toast.makeText(this, "This is the first chapter", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun chapterListInit() {
        chapterList = intent.getSerializableExtra("chapterList") as ArrayList<Chapter>
        if(chapterList.isEmpty()){
            getChapterList(mangaUrl)
            viewModel.chapterList.observe(this, Observer{
                chapterList = it as ArrayList<Chapter>
                for(i in 0 until chapterList.size){
                    if(chapterList[i].chapterUrl == chapUrl){
                        chapPost = i
                        break
                    }
                }
            })
        }
        if(chapterList.isNotEmpty()){
            for(i in 0 until chapterList.size){
                if(chapterList[i].chapterUrl == chapUrl){
                    chapPost = i
                    break
                }
            }
        }
    }

    private fun bottomSheetInit() {
        // Initialize the BottomSheetDialog
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.chapter_list_dialog)

        // Initialize the RecyclerView in the dialog
        val chapterListRv = bottomSheetDialog.findViewById<RecyclerView>(R.id.chapterList)
        chapterListRv?.layoutManager = LinearLayoutManager(this)
        bottomSheetRvAdapter = BottomSheetRvAdapter(chapterList){ chapter, position ->
            val intent = Intent(this@ChapContentActivity, ChapContentActivity::class.java)
            intent.putExtra("chapterPost", position)
            intent.putExtra("mangaUrl", mangaUrl)
            intent.putExtra("chapterList",chapterList)
            intent.putExtra("chapUrl",chapter.chapterUrl)
            startActivity(intent)
        }
        chapterListRv?.adapter = bottomSheetRvAdapter

        // Set the OnClickListener for the BottomSheetDragHandleView
        // Set the OnClickListener for the BottomSheetDragHandleView
        bottomSheet.setOnClickListener {
            bottomSheetDialog.show()

            // Scroll to the current chapter position
            chapterListRv?.post {
                val smoothScroller = object : androidx.recyclerview.widget.LinearSmoothScroller(this) {
                    override fun getVerticalSnapPreference(): Int {
                        return SNAP_TO_START
                    }
                }
                smoothScroller.targetPosition = chapPost
                chapterListRv?.layoutManager?.startSmoothScroll(smoothScroller)
            }
        }
    }

    private fun initialize() {
        mangaUrl = intent.getStringExtra("mangaUrl").toString()
        chapPost = intent.getIntExtra("chapterPost",0)
        chapUrl = intent.getStringExtra("chapUrl").toString()
        Log.d("ChapContentActivity", "initialize() : $mangaUrl | $chapPost | $chapUrl")
        //val toolBar = findViewById(R.id.toolbar)
        bottomSheet = findViewById(R.id.chapter_list)
        imageRv = findViewById(R.id.chapterContentRv)
        nextButton = findViewById(R.id.nextChapterButton)
        prevButton = findViewById(R.id.prevChapterButton)
        backButton = findViewById(R.id.back_button)
        INSTANCE = AppDatabase.getInstance(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        backButton.setOnClickListener(){
            finish()
        }

    }

    private fun getChapterList(url : String){
        viewModel.getChapterList(url)
    }
    private fun getChapterImages(url : String){
        viewModel.getChapterImages(url)
    }
}
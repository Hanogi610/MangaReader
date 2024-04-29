package com.example.mangareader.activity

import adapter.BottomSheetRvAdapter
import adapter.ChapterContentRvAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
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
    lateinit var bottomSheet : TextView
    lateinit var imageRv : RecyclerView
    lateinit var nextButton: ImageButton
    lateinit var prevButton: ImageButton
    lateinit var chapterList : ArrayList<Chapter>
    lateinit var chapterContentRvAdapter: ChapterContentRvAdapter
    lateinit var chapterImages :List<String>
    lateinit var INSTANCE : AppDatabase
    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var bottomSheetRvAdapter: BottomSheetRvAdapter
    lateinit var chapterListRv : RecyclerView
    lateinit var bottomBar : LinearLayout
    lateinit var chapTitle : String
    var chapPost = 0
    private val viewModel : ChapContentViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chap_content)

        initialize()

        bottomSheetInit()

        chapterListInit()

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
            Log.d("ChapContentActivity", "nextButton.setOnClickListener(): ${chapterList.size} | $chapPost")
            if(chapPost < chapterList.size){
                chapPost++
                val intentC = Intent(this, ChapContentActivity::class.java)
                intentC.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                intentC.putExtra("mangaUrl", mangaUrl)
                intentC.putExtra("chapterList", chapterList)
                intentC.putExtra("chapPost", chapPost)
                intentC.putExtra("chapTitle",chapterList.get(chapPost).chapterTitle)
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
                intentC.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                intentC.putExtra("mangaUrl", mangaUrl)
                intentC.putExtra("chapterList", chapterList)
                intentC.putExtra("chapPost", chapPost)
                intentC.putExtra("chapUrl",chapterList.get(chapPost).chapterUrl)
                intentC.putExtra("chapTitle",chapterList.get(chapPost).chapterTitle)
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
        Log.d("ChapContentActivity", "chapterListInit(): ${chapterList.size}")
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
                bottomSheetRvAdapter.updateData(chapterList)
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
        bottomSheet.text = chapTitle
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout)

        // Initialize the RecyclerView in the dialog
        chapterListRv = bottomSheetDialog.findViewById<RecyclerView>(R.id.bottom_sheet_rv)!!
        chapterListRv.layoutManager = LinearLayoutManager(this)
        bottomSheetRvAdapter = BottomSheetRvAdapter(chapterList, chapPost){ chapter, position ->

            val intent = Intent(this@ChapContentActivity, ChapContentActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("chapterPost", position)
            intent.putExtra("mangaUrl", mangaUrl)
            intent.putExtra("chapterList",chapterList)
            intent.putExtra("chapUrl",chapter.chapterUrl)
            intent.putExtra("chapTitle",chapter.chapterTitle)
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                Log.d("ChapContentActivity", "bottomSheetRvAdapter.setOnClickListener(): $mangaUrl | ${chapter.chapterTitle} | ${chapter.chapterUrl} | ${Date().time} | $position")
                INSTANCE.historyMangaDAO().updateHistoryManga(mangaUrl, chapterList.get(position).chapterTitle.toString(), chapterList.get(position).chapterUrl.toString(),Date().time,position)
            }
            startActivity(intent)
        }
        chapterListRv.adapter = bottomSheetRvAdapter

        // Set the OnClickListener for the BottomSheetDragHandleView

        bottomSheet.setOnClickListener {
            Log.d("ChapContentActivity", "bottomSheet.setOnClickListener()")
            bottomSheetDialog.show()

            // Scroll to the current chapter position
            chapterListRv.post {
                val smoothScroller = object : androidx.recyclerview.widget.LinearSmoothScroller(this) {
                    override fun getVerticalSnapPreference(): Int {
                        return SNAP_TO_START
                    }
                }
                smoothScroller.targetPosition = chapPost
                chapterListRv.layoutManager?.startSmoothScroll(smoothScroller)
            }
        }
    }

    private fun initialize() {
        mangaUrl = intent.getStringExtra("mangaUrl").toString()
        chapPost = intent.getIntExtra("chapterPost",0)
        chapUrl = intent.getStringExtra("chapUrl").toString()
        chapTitle = intent.getStringExtra("chapTitle").toString()
        chapterList = intent.getSerializableExtra("chapterList") as ArrayList<Chapter>
        Log.d("ChapContentActivity", "initialize() : $mangaUrl | $chapPost | $chapUrl")
        bottomSheet = findViewById(R.id.bottom_sheet_button)
        imageRv = findViewById(R.id.chapterContentRv)
        nextButton = findViewById(R.id.nextChapterButton)
        prevButton = findViewById(R.id.prevChapterButton)
        backButton = findViewById(R.id.back_button)
        INSTANCE = AppDatabase.getInstance(this)
        bottomBar = findViewById(R.id.bottomBar)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        backButton.setOnClickListener(){
            finish()
        }

        imageRv.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_UP) {
                    if (toolbar.visibility == View.VISIBLE) {
                        toolbar.visibility = View.GONE
                    } else {
                        toolbar.visibility = View.VISIBLE
                    }
                    if(bottomBar.visibility == View.VISIBLE){
                        bottomBar.visibility = View.GONE
                    }else{
                        bottomBar.visibility = View.VISIBLE
                    }
                }
                return super.onInterceptTouchEvent(rv, e)
            }
        })

    }

    private fun getChapterList(url : String){
        viewModel.getChapterList(url)
    }
    private fun getChapterImages(url : String){
        viewModel.getChapterImages(url)
    }
}
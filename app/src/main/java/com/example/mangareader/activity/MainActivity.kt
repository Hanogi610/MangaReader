package com.example.mangareader.activity

import adapter.MainBottomNavigationViewPagerAdapter
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.mangareader.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.HomepageManga
import scraper.NettruyenJsoup

class MainActivity : AppCompatActivity() {



    private val bottomNaviationView : BottomNavigationView by lazy {
        findViewById(R.id.main_bottom_navigation)
    }
    private val viewPager : ViewPager2 by lazy {
        findViewById(R.id.main_view_pager)
    }

    private val mainBottomNavigationViewPagerAdapter = MainBottomNavigationViewPagerAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager.adapter = mainBottomNavigationViewPagerAdapter
        viewPager.isUserInputEnabled = false
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNaviationView.selectedItemId = when (position) {
                    0 -> R.id.main_bottom_navigation_home
                    1 -> R.id.main_bottom_navigation_history
                    2 -> R.id.main_bottom_navigation_library
                    3 -> R.id.main_bottom_navigation_settings
                    else -> R.id.main_bottom_navigation_home
                }
            }
        })
        bottomNaviationView.setOnNavigationItemSelectedListener {
            viewPager.currentItem = when (it.itemId) {
                R.id.main_bottom_navigation_home -> 0
                R.id.main_bottom_navigation_history -> 1
                R.id.main_bottom_navigation_library -> 2
                R.id.main_bottom_navigation_settings -> 3
                else -> 0
            }
            true
        }

    }
}
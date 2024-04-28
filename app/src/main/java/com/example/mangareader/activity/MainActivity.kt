package com.example.mangareader.activity

import adapter.MainBottomNavigationViewPagerAdapter
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.mangareader.R
import com.google.android.material.bottomnavigation.BottomNavigationView

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

        val poliicy = StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .build()
        StrictMode.setThreadPolicy(poliicy)

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
package com.example.mangareader.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mangareader.fragment.HistoryFragment
import com.example.mangareader.fragment.HomeFragment
import com.example.mangareader.fragment.LibraryFragment

class MainBottomNavigationViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> HistoryFragment()
            2 -> LibraryFragment()
            else -> HomeFragment()
        }
    }
}
package adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mangareader.fragment.LatestMangaFragment
import com.example.mangareader.fragment.MangaRankingsFragment
import com.example.mangareader.fragment.MangaTypeFragment
import com.example.mangareader.fragment.SearchFragment

class HomeViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> LatestMangaFragment()
                1 -> SearchFragment()
                2 -> MangaRankingsFragment()
//                3 -> MangaTypeFragment()
                else -> LatestMangaFragment()
            }
        }
}
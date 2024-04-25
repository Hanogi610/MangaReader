package adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import fragment.LatestMangaFragment
import fragment.MangaRankingsFragment
import fragment.MangaTypeFragment

class HomeViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> LatestMangaFragment()
                1 -> MangaRankingsFragment()
                2 -> MangaTypeFragment()
                else -> LatestMangaFragment()
            }
        }
}
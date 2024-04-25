package adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import fragment.HistoryFragment
import fragment.HomeFragment
import fragment.LatestMangaFragment
import fragment.LibraryFragment
import fragment.MiscellaneousFragment

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
            3 -> MiscellaneousFragment()
            else -> HomeFragment()
        }
    }
}
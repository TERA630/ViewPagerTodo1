package com.example.yoshi.viewpagertodo1

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

// 依存関係　Of　DayPagerAdapter
//　上流　MainActivity から　引数をもらっている｡
//　下流　RecyclerFragmentの　Instance作成

class MainPagerAdapter(fragmentManager: FragmentManager, val model: MainViewModel) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        val filter = model.tagList[position]
        return MainFragment.newInstance(filter)
    }

    override fun getCount(): Int = model.tagList.size
    override fun getPageTitle(position: Int): CharSequence? {
        return model.tagList[position]
    }
}
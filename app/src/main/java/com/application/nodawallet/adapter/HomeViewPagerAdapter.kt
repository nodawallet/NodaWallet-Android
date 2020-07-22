package com.application.nodawallet.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.application.nodawallet.fragment.InvestmentFragment
import com.application.nodawallet.fragment.TokenFragment

class HomeViewPagerAdapter internal constructor(fm: FragmentActivity) : FragmentStateAdapter(fm) {

    private val COUNT = 2


    override fun getItemCount(): Int {
        return COUNT
    }

    override fun createFragment(position: Int): Fragment {
        var fragment = Fragment()
        when (position) {
            0 -> fragment =  TokenFragment()
            1 ->  fragment =  InvestmentFragment()

        }
        return fragment

    }
}


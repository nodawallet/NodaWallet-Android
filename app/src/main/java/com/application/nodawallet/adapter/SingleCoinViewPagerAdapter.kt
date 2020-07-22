package com.application.nodawallet.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.application.nodawallet.fragment.*

class SingleCoinViewPagerAdapter internal constructor(fm: FragmentActivity) : FragmentStateAdapter(fm) {

    private val COUNT = 3


    override fun getItemCount(): Int {
        return COUNT
    }

    override fun createFragment(position: Int): Fragment {
        var fragment = Fragment()
        when (position) {
            0 -> fragment =  SingleCoinPhraseFragment()
            1 ->  fragment =  SingleCoinPrivateKeyFragment()
            2 ->  fragment =  SingleCoinAddressFragment()

        }
        return fragment

    }
}
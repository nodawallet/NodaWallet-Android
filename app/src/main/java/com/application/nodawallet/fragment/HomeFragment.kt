package com.application.nodawallet.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity.Companion.mainBack
import com.application.nodawallet.adapter.HomeViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var adapter = HomeViewPagerAdapter(activity!!)
        viewPagerHome.adapter = adapter

        textTokens.setOnClickListener {
            if (viewPagerHome.currentItem != 0){
                viewPagerHome.setCurrentItem(0,true)
            }


        }

        textCollectibles.setOnClickListener {
            if (viewPagerHome.currentItem != 1){
                viewPagerHome.setCurrentItem(1,true)
            }

        }

        viewPagerHome.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {

                if(position == 0){
                    textTokens.setTextColor(ContextCompat.getColor(activity!!,R.color.hometokentext))
                    textTokens.setBackgroundResource(R.drawable.corner_left)
                    textCollectibles.setTextColor(ContextCompat.getColor(activity!!,R.color.hometabinvest))
                    textCollectibles.setBackgroundResource(R.drawable.corner_right)
                }
                else if (position == 1){
                    textTokens.setTextColor(ContextCompat.getColor(activity!!,R.color.hometabinvest))
                    textTokens.setBackgroundResource(R.drawable.corner_left_gray)
                    textCollectibles.setTextColor(ContextCompat.getColor(activity!!,R.color.hometokentext))
                    textCollectibles.setBackgroundResource(R.drawable.corner_right_grey)
                }

                super.onPageSelected(position)
            }
        })



    }

    override fun onResume() {
        super.onResume()
        mainBack = 0


    }




}

package com.application.nodawallet.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.activity.MainActivity.Companion.mainBack
import com.application.nodawallet.adapter.*
import kotlinx.android.synthetic.main.fragment_history.*

class TransHistoryFragment : BaseFragment(R.layout.fragment_history) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val adapter = TransHistoryViewPagerAdapter(activity!!)
        viewPagerTransHistory.adapter = adapter

        textSend.setOnClickListener {
            if (viewPagerTransHistory.currentItem != 0){
                viewPagerTransHistory.setCurrentItem(0,true)
            }
        }

        textReceive.setOnClickListener {
            if (viewPagerTransHistory.currentItem != 1){
                viewPagerTransHistory.setCurrentItem(1,true)
            }
        }

        textExchange.setOnClickListener {
            if (viewPagerTransHistory.currentItem != 2){
                viewPagerTransHistory.setCurrentItem(2,true)
            }
        }

        viewPagerTransHistory.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {

                if(position == 0){
                    textSend.setTextColor(ContextCompat.getColor(activity!!,R.color.dexexback))
                    textSend.setBackgroundResource(R.drawable.corner_left)

                    textReceive.setTextColor(ContextCompat.getColor(activity!!,R.color.dextextcolor))
                    textReceive.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.purchaserect))

                    textExchange.setTextColor(ContextCompat.getColor(activity!!,R.color.dextextcolor))
                    textExchange.setBackgroundResource(R.drawable.corner_right)
                }
                else if (position == 1){
                    textSend.setTextColor(ContextCompat.getColor(activity!!,R.color.dextextcolor))
                    textSend.setBackgroundResource(R.drawable.corner_left_gray)

                    textReceive.setTextColor(ContextCompat.getColor(activity!!,R.color.dexexback))
                    textReceive.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.bottomactive))

                    textExchange.setTextColor(ContextCompat.getColor(activity!!,R.color.dextextcolor))
                    textExchange.setBackgroundResource(R.drawable.corner_right)
                }
                else if (position == 2){
                    textSend.setTextColor(ContextCompat.getColor(activity!!,R.color.dextextcolor))
                    textSend.setBackgroundResource(R.drawable.corner_left_gray)

                    textReceive.setTextColor(ContextCompat.getColor(activity!!,R.color.dextextcolor))
                    textReceive.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.purchaserect))

                    textExchange.setTextColor(ContextCompat.getColor(activity!!,R.color.dexexback))
                    textExchange.setBackgroundResource(R.drawable.corner_right_grey)


                }
                super.onPageSelected(position)
            }
        })

        imgBackHistory.setOnClickListener {
            (context as MainActivity).push(SettingsFragment())
        }

    }


    override fun onResume() {
        super.onResume()

        mainBack = 2

    }

}
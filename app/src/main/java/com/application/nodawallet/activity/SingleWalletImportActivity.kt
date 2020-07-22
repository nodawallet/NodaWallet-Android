package com.application.nodawallet.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.application.nodawallet.R
import com.application.nodawallet.adapter.SingleCoinViewPagerAdapter
import kotlinx.android.synthetic.main.activity_single_coin_import.*

class SingleWalletImportActivity : BaseActivity() {

    var tokenName = ""
    var tokenId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_coin_import)

        val intent = intent
        if (intent !=null){
            tokenName = intent.getStringExtra("tokenName") ?: ""
            tokenId = intent.getIntExtra("tokenId",-1)
        }

        textHeaderImport.text = getString(R.string.imports)+" "+tokenName

        val adapter = SingleCoinViewPagerAdapter(this)
        viewPagerSingleCoin.adapter = adapter

        imgBackImport.setOnClickListener {
            onBackPressed()
        }

        textPhrase.setOnClickListener {
            if (viewPagerSingleCoin.currentItem != 0){
                viewPagerSingleCoin.setCurrentItem(0,true)
            }
        }

        textPrivateKey.setOnClickListener {
            if (viewPagerSingleCoin.currentItem != 1){
                viewPagerSingleCoin.setCurrentItem(1,true)
            }
        }

        textAddress.setOnClickListener {
            if (viewPagerSingleCoin.currentItem != 2){
                viewPagerSingleCoin.setCurrentItem(2,true)
            }
        }

        viewPagerSingleCoin.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {

                if(position == 0){


                    textPhrase.setTextColor(ContextCompat.getColor(this@SingleWalletImportActivity,R.color.dexexback))
                    textPhrase.setBackgroundResource(R.drawable.corner_left_orange)

                    textPrivateKey.setTextColor(ContextCompat.getColor(this@SingleWalletImportActivity,R.color.dextextcolor))
                    textPrivateKey.setBackgroundColor(ContextCompat.getColor(this@SingleWalletImportActivity,R.color.hometabright))

                    textAddress.setTextColor(ContextCompat.getColor(this@SingleWalletImportActivity,R.color.dextextcolor))
                    textAddress.setBackgroundResource(R.drawable.corner_right)
                }
                else if (position == 1){
                    textPhrase.setTextColor(ContextCompat.getColor(this@SingleWalletImportActivity,R.color.dextextcolor))
                    textPhrase.setBackgroundResource(R.drawable.corner_left_gray)

                    textPrivateKey.setTextColor(ContextCompat.getColor(this@SingleWalletImportActivity,R.color.dexexback))
                    textPrivateKey.setBackgroundColor(ContextCompat.getColor(this@SingleWalletImportActivity,R.color.bottomactive))

                    textAddress.setTextColor(ContextCompat.getColor(this@SingleWalletImportActivity,R.color.dextextcolor))
                    textAddress.setBackgroundResource(R.drawable.corner_right)
                }
                else if (position == 2){
                    textPhrase.setTextColor(ContextCompat.getColor(this@SingleWalletImportActivity,R.color.dextextcolor))
                    textPhrase.setBackgroundResource(R.drawable.corner_left_gray)

                    textPrivateKey.setTextColor(ContextCompat.getColor(this@SingleWalletImportActivity,R.color.dextextcolor))
                    textPrivateKey.setBackgroundColor(ContextCompat.getColor(this@SingleWalletImportActivity,R.color.hometabright))

                    textAddress.setTextColor(ContextCompat.getColor(this@SingleWalletImportActivity,R.color.dexexback))
                    textAddress.setBackgroundResource(R.drawable.corner_right_orange)


                }
                super.onPageSelected(position)
            }
        })

    }
}

package com.application.nodawallet.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.application.nodawallet.R
import com.application.nodawallet.fragment.*
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.PermissionUtils
import com.application.nodawallet.utils.UtilsDefault
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    companion object {
        lateinit var bottomNav:BottomNavigationView
        var mainBack = 0
    }

    private var doubleBackToExitPressedOnce = false

    var PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        bottomNav =  findViewById(R.id.bottomNavigation)
        bottomNavigation?.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        push(HomeFragment())
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.wallet -> {
               push(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            /*R.id.card -> {
                push(BuyCryptoFragment())
                return@OnNavigationItemSelectedListener true
            }*/
            R.id.scan -> {

                val cointype2 = UtilsDefault.getSharedPreferenceString(Constants.COINTYPE)
                if (cointype2 == "public"){
                    Toast.makeText(this,getString(R.string.transaction_publicaddress),Toast.LENGTH_SHORT).show()
                    return@OnNavigationItemSelectedListener true

                }
                PermissionUtils().permissionList(PERMISSIONS,this,object : PermissionUtils.PermissionCheck{
                    override fun onSuccess(result: String?) {
                        val intent = Intent(this@MainActivity,WalletConnectActivity::class.java)
                        startActivity(intent)
                    }

                })

                return@OnNavigationItemSelectedListener true
            }
            R.id.dex -> {
                push(ExchangeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.settings -> {
                push(SettingsFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    fun popBackStackImmediate() {
        val fragmentManager = supportFragmentManager
        fragmentManager.popBackStack()
        fragmentManager.executePendingTransactions()
    }

    fun popAllBackstack() {
        val fragmentManager = supportFragmentManager
        val backCount = fragmentManager.backStackEntryCount
        if (backCount > 0) {
            fragmentManager.popBackStackImmediate(
                fragmentManager.getBackStackEntryAt(0).id,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
    }

    fun push(fragment: Fragment, title: String?) {
        val fragmentManager = supportFragmentManager
        val tag = fragment.javaClass.canonicalName

        if (title != null) {
            try {
                fragmentManager.beginTransaction()
                    .replace(R.id.mainFrameContainer, fragment, tag)
                    .addToBackStack(title)
                    .commit()
            } catch (ile: IllegalStateException) {
                fragmentManager.beginTransaction()
                    .replace(R.id.mainFrameContainer, fragment, tag)
                    .addToBackStack(title)
                    .commitAllowingStateLoss()
            }

        } else {
            try {
                fragmentManager.beginTransaction()
                    .replace(R.id.mainFrameContainer, fragment, tag).commit()
            } catch (ile: IllegalStateException) {
                fragmentManager.beginTransaction()
                    .replace(R.id.mainFrameContainer, fragment, tag).commitAllowingStateLoss()
                //  .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )

            }

        }

    }

    fun pushanim(fragment: Fragment, title: String?) {
        val fragmentManager = supportFragmentManager
        val tag = fragment.javaClass.canonicalName

        if (title != null) {
            try {
                fragmentManager.beginTransaction()
                    .setCustomAnimations( android.R.anim.fade_in , android.R.anim.fade_out)
                    .replace(R.id.mainFrameContainer, fragment, tag)
                    .addToBackStack(title)
                    .commit()
            } catch (ile: IllegalStateException) {
                fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.mainFrameContainer, fragment, tag)
                    .addToBackStack(title)
                    .commitAllowingStateLoss()
            }

        } else {
            try {
                fragmentManager.beginTransaction()
                    .setCustomAnimations( android.R.anim.fade_in , android.R.anim.fade_out)
                    //.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.mainFrameContainer, fragment, tag)
                    .commit()
            } catch (ile: IllegalStateException) {
                fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    //.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.mainFrameContainer, fragment, tag)
                    .commitAllowingStateLoss()
                //  .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )

            }

        }

    }

    override fun onBackPressed() {


        if (mainBack == 1){
            push(HomeFragment())
            bottomNav.menu.findItem(R.id.wallet).isChecked = true
        }
        else if (mainBack == 2){
            push(SettingsFragment())
            bottomNav.menu.findItem(R.id.settings).isChecked = true
        }
        else if (mainBack == 3){
            push(MainWalletFragment())
        }

        else {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, getString(R.string.click_back_again), Toast.LENGTH_SHORT).show()

            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)

        }

    }

    fun push(fragment: Fragment?) {
        if (fragment == null) {
            return
        }
        push(fragment, null)
    }

}

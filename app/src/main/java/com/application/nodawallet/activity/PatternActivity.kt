package com.application.nodawallet.activity

import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.application.nodawallet.R
import com.application.nodawallet.fragment.BackupPatternFragment
import com.application.nodawallet.fragment.ShowPatternFragment

class PatternActivity : BaseActivity() {

    companion object{
        var patternBack = 0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_pattern)

        push(BackupPatternFragment())

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
                    .replace(R.id.patternFrameContainer, fragment, tag)
                    .addToBackStack(title)
                    .commit()
            } catch (ile: IllegalStateException) {
                fragmentManager.beginTransaction()
                    .replace(R.id.patternFrameContainer, fragment, tag)
                    .addToBackStack(title)
                    .commitAllowingStateLoss()
            }

        } else {
            try {
                fragmentManager.beginTransaction()
                    .replace(R.id.patternFrameContainer, fragment, tag).commit()
            } catch (ile: IllegalStateException) {
                fragmentManager.beginTransaction()
                    .replace(R.id.patternFrameContainer, fragment, tag).commitAllowingStateLoss()
                //  .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )

            }

        }

    }


    fun push(fragment: Fragment?) {
        if (fragment == null) {
            return
        }
        push(fragment, null)
    }

    override fun onBackPressed() {

        if (patternBack == 0){
            super.onBackPressed()
        }
        else if (patternBack == 1){
            push(BackupPatternFragment())
        }
        else if (patternBack == 2){
            push(ShowPatternFragment())
        }


    }



}

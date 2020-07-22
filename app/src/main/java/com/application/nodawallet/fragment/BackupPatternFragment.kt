package com.application.nodawallet.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast

import com.application.nodawallet.R
import com.application.nodawallet.activity.PatternActivity
import com.application.nodawallet.activity.ImportListActivity
import com.application.nodawallet.activity.PatternActivity.Companion.patternBack
import kotlinx.android.synthetic.main.activity_pattern.*
import kotlinx.android.synthetic.main.fragment_backup_pattern.*


class BackupPatternFragment : BaseFragment(R.layout.fragment_backup_pattern) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val slide_up: Animation = AnimationUtils.loadAnimation(activity, R.anim.slide_up)

        var fade_in =  AlphaAnimation(0.0F,1.0F)
        fade_in.duration = 1200
        textPrivate.startAnimation(fade_in)
        textKeys.startAnimation(fade_in)

        consIcon.startAnimation(slide_up)

        btnCreateWallet.setOnClickListener {
            consCreateWallet.visibility =  View.GONE
            consConset.visibility =  View.VISIBLE
        }

        btnContinue.setOnClickListener {

            if (checkConsent.isChecked){
                (context as PatternActivity).push(ShowPatternFragment())
            }
            else {
                Toast.makeText(activity,getString(R.string.accept_conditions),Toast.LENGTH_SHORT).show()
            }

        }

        mImportwallet.setOnClickListener {
           activity!!.startActivity(Intent(activity!!, ImportListActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        patternBack = 0
        (context as PatternActivity).consHeader.visibility = View.GONE
    }

}

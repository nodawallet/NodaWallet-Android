package com.application.nodawallet.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.application.nodawallet.R
import com.application.nodawallet.activity.AboutActivity
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.activity.MainActivity.Companion.mainBack
import kotlinx.android.synthetic.main.fragment_about.*


class AboutFragment : BaseFragment(R.layout.fragment_about) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgBackAbout?.setOnClickListener {
            (context as MainActivity).push(SettingsFragment())

        }

        rlAboutApp?.setOnClickListener {
            val intent = Intent(activity,AboutActivity::class.java)
            intent.putExtra("data",getString(R.string.aboutapp))
            intent.putExtra("request","aboutus")
            startActivity(intent)
        }

        rlTerms?.setOnClickListener {
            val intent = Intent(activity,AboutActivity::class.java)
            intent.putExtra("data",getString(R.string.terms))
            intent.putExtra("request","termsandconditions")
            startActivity(intent)
        }

        rlPrivacy?.setOnClickListener {
            val intent = Intent(activity,AboutActivity::class.java)
            intent.putExtra("data",getString(R.string.privacypolicy))
            intent.putExtra("request","privacypolicy")
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        mainBack = 2
    }

}

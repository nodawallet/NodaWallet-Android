package com.application.nodawallet.fragment

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.activity.MainActivity.Companion.mainBack
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.LocaleHelper
import com.application.nodawallet.utils.UtilsDefault
import kotlinx.android.synthetic.main.fragment_language.*


class LanguageFragment : BaseFragment(R.layout.fragment_language) {


    var resource: Resources? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (UtilsDefault.getSharedPreferenceString(Constants.LANGUAGE) == null){
            UtilsDefault.updateSharedPreferenceString(Constants.LANGUAGE,"en")
            radioEnglish.isChecked = true
        }

        if (UtilsDefault.getSharedPreferenceString(Constants.LANGUAGE) == "ru"){
            radioRussian.isChecked = true
        }
        else{
            radioEnglish.isChecked = true
        }

        imgBackLanguage.setOnClickListener {
            (context as MainActivity).push(SettingsFragment())
        }


        radioRussian.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {

                LocaleHelper().setLocale(activity!!, "ru")
                resource = context!!.resources

                UtilsDefault.updateSharedPreferenceString(Constants.LANGUAGE,"ru")

                val intent =  Intent(activity,MainActivity::class.java)
                startActivity(intent)
                activity!!.finish()


            }

        }

        radioEnglish.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {

                UtilsDefault.updateSharedPreferenceString(Constants.LANGUAGE,"en")

                LocaleHelper().setLocale(activity!!, "en")
                resource = context!!.resources

                val intent =  Intent(activity,MainActivity::class.java)
                startActivity(intent)
                activity!!.finish()
            }

        }





    }

    override fun onResume() {
        super.onResume()

        mainBack = 2

    }

}

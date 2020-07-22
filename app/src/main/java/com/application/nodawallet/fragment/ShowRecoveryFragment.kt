package com.application.nodawallet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.application.nodawallet.R
import com.application.nodawallet.adapter.PatternAdapter
import com.application.nodawallet.utils.UtilsDefault
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.android.synthetic.main.fragment_show_pattern.*
import kotlinx.android.synthetic.main.fragment_show_recovery.*


class ShowRecoveryFragment : BaseFragment(R.layout.fragment_show_recovery) {

    private var patternAdapter: PatternAdapter? = null
    var phrase = ""

    var list =  arrayListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        if (bundle!=null){
            phrase = bundle.getString("phrase") ?: ""
        }

        val lstValues: List<String> = phrase.split(" ").map { it -> it.trim() }
        list.clear()
        list.addAll(lstValues)

        textCopyPhrase.setOnClickListener {
            UtilsDefault.copyToClipboard(activity!!,phrase)
            Toast.makeText(activity,getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()

        }


        val layoutManager =  FlexboxLayoutManager(activity)
        layoutManager.justifyContent =  JustifyContent.CENTER
        layoutManager.flexDirection =  FlexDirection.ROW
        layoutManager.flexWrap =  FlexWrap.WRAP
        recycleShowPhrase.layoutManager =  layoutManager
        patternAdapter = PatternAdapter(activity!!,list)
        recycleShowPhrase.adapter =  patternAdapter


    }

}

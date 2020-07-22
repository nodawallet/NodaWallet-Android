package com.application.nodawallet.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.activity.PatternActivity
import com.application.nodawallet.activity.PatternActivity.Companion.patternBack
import com.application.nodawallet.adapter.PatternAdapter
import com.application.nodawallet.adapter.ShowPatternHomeAdapter
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.utils.UtilsDefault
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import io.github.novacrypto.bip39.MnemonicGenerator
import io.github.novacrypto.bip39.Words
import io.github.novacrypto.bip39.wordlists.English
import kotlinx.android.synthetic.main.activity_pattern.*
import kotlinx.android.synthetic.main.fragment_show_pattern.*
import java.security.SecureRandom


class ShowPatternFragment : BaseFragment(R.layout.fragment_show_pattern) {

    private var patternAdapter: ShowPatternHomeAdapter? = null

    var list =  arrayListOf<String>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mnemonic = generateNewMnemonic(Words.TWELVE)

        val lstValues: List<String> = mnemonic?.split(" ")!!.map { it -> it.trim() }
        list.clear()
        list.addAll(lstValues)


        val layoutManager =  FlexboxLayoutManager(activity)
        layoutManager.justifyContent =  JustifyContent.CENTER
        layoutManager.flexDirection =  FlexDirection.ROW
        layoutManager.flexWrap =  FlexWrap.WRAP
        recyclePhrase.layoutManager =  layoutManager
        patternAdapter = ShowPatternHomeAdapter(activity!!,list)
        recyclePhrase.adapter =  patternAdapter

        btnProceed.setOnClickListener {

            val fragment =  VerifyPatternFragment()
            val bundle = Bundle()
            bundle.putString("phrase",mnemonic)
            fragment.arguments = bundle
            (context as PatternActivity).push(fragment)
        }

        textCopy.setOnClickListener {

            val sb = StringBuilder()
            var c = ""

            for (i in 0..list.size.minus(1)){

                sb.append(list[i]+" ")
                c = sb.toString()
            }

            UtilsDefault.copyToClipboard(activity!!,c)
            Toast.makeText(activity,activity!!.getString(R.string.copied_to_clipboard),Toast.LENGTH_SHORT).show()

        }

    }
    private fun generateNewMnemonic(wordCount: Words): String? {
        val sb = java.lang.StringBuilder()
        val entropy = ByteArray(Words.TWELVE.byteLength())
        SecureRandom().nextBytes(entropy)
        MnemonicGenerator(English.INSTANCE)
            .createMnemonic(entropy) { charSequence: CharSequence? -> sb.append(charSequence) }
       // println("sb="+sb.toString())

        return sb.toString()
    }

    override fun onResume() {
        super.onResume()
        patternBack = 1
        (context as PatternActivity).consHeader.visibility = View.VISIBLE
    }

}

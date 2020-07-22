package com.application.nodawallet.fragment

import android.graphics.Bitmap
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.application.nodawallet.R
import com.application.nodawallet.activity.BaseActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix

open class BaseFragment(layoutId:Int) :Fragment(layoutId) {

    fun showProgress() {
        showProgress(R.string.loading)
    }

    fun showProgress(stringId: Int) {
        (this.activity as BaseActivity?)?.showProgress(stringId)
    }
    fun hideProgress() {
        val activity = activity as BaseActivity?
        activity?.hideProgress()
    }

    fun cleardata(v: EditText) {
        v.setText("")

    }


}
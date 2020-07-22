package com.application.nodawallet.activity

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.application.nodawallet.R
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.CustomTextViewNormal
import com.application.nodawallet.utils.LocaleHelper
import com.application.nodawallet.utils.UtilsDefault
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*

abstract class BaseActivity :AppCompatActivity() {

    var mcontext: Context? = null
    protected var mHandler = Handler()
    lateinit var dialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        mcontext = this
        dialog = Dialog(this@BaseActivity)

    }

    fun showProgress() {
        showProgress(getString(R.string.loading))
    }

    fun showProgress(messageId: Int) {
        showProgress(getString(messageId))
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper().onAttach(newBase!!))

    }

    fun showProgress(message: CharSequence) {
        if (applicationContext != null) {
            mHandler.post {
                try {
                    dialog = Dialog(this@BaseActivity)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.setContentView(R.layout.progress_load)
                    dialog.setCancelable(false)
                    dialog.show()
                     val progress : CustomTextViewNormal = dialog.findViewById(R.id.text_progressupdate)
                     progress.text = message
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

    }

    fun hideProgress() {
        mHandler.post {
            try {
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun cleardata(v: EditText) {
        v.setText("")

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (overrideConfiguration != null) {
            val uiMode = overrideConfiguration.uiMode
            overrideConfiguration.setTo(baseContext.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
        super.applyOverrideConfiguration(overrideConfiguration)

    }

}
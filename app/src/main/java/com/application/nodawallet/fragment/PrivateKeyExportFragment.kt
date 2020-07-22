package com.application.nodawallet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.activity.MainActivity.Companion.mainBack
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.utils.UtilsDefault
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_receive.*
import kotlinx.android.synthetic.main.fragment_private_key_export.*


class PrivateKeyExportFragment : BaseFragment(R.layout.fragment_private_key_export) {
    var key = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        if (bundle!=null){
            key = bundle.getString("phrase") ?:""
        }

        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitmatrix =  multiFormatWriter.encode(key, BarcodeFormat.QR_CODE,200,200)
            val barcodeEncoder = BarcodeEncoder()
            val bmp = barcodeEncoder.createBitmap(bitmatrix)
            imgQrcodekey?.setImageBitmap(bmp)
            textKey?.text = key

        }

        catch (e:Exception){
            e.printStackTrace()
        }

        imgBackPrivate.setOnClickListener {
            (context as MainActivity).push(MainWalletFragment())
        }

        consCopykey.setOnClickListener {
            UtilsDefault.copyToClipboard(activity!!,key)
            Toast.makeText(activity,activity!!.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()

        }

    }

    override fun onResume() {
        super.onResume()
        mainBack = 3
    }
}

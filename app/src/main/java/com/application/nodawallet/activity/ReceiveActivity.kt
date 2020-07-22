package com.application.nodawallet.activity

import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.application.nodawallet.R
import com.application.nodawallet.utils.UtilsDefault
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_receive.*
import kotlinx.android.synthetic.main.view_amount_popup.*

class ReceiveActivity : BaseActivity() {

    var address = ""
    var coinsymbol = ""
    var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive)

        val intent = intent
        if (intent != null) {
            address = intent.getStringExtra("address") ?: ""
            coinsymbol = intent.getStringExtra("coinsymbol") ?: ""
        }

        val multiFormatWriter = MultiFormatWriter()
        try {

            val bitmatrix = multiFormatWriter.encode(address, BarcodeFormat.QR_CODE, 250, 250)
            val barcodeEncoder = BarcodeEncoder()
            val bmp = barcodeEncoder.createBitmap(bitmatrix)
            imgQrcode?.setImageBitmap(bmp)
            textAddress?.text = address

        } catch (e: Exception) {
            e.printStackTrace()
        }


        consCopy.setOnClickListener {
            UtilsDefault.copyToClipboard(this, address)
            Toast.makeText(this, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
        }

        consShare.setOnClickListener {
            val mypubtext = getString(R.string.share_text)+" "+coinsymbol+" :"
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, mypubtext+address)
            startActivity(Intent.createChooser(intent, "Share via"))

        }

        constraintLayout5.setOnClickListener {
            mSetAmount()
        }

        imgBackReceive.setOnClickListener {
            onBackPressed()
        }
    }

    fun mSetAmount() {
        var v_dialog = Dialog(this)
        v_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        v_dialog.setCancelable(false)
        v_dialog.setContentView(R.layout.view_amount_popup)

        v_dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        v_dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        v_dialog.window!!.setLayout(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )
        v_dialog.show()

        v_dialog.mSubmitBtn?.setOnClickListener {
            v_dialog.dismiss()
        }

        v_dialog.mClosebtn?.setOnClickListener {
            v_dialog.dismiss()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}

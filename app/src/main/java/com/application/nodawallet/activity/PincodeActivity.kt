package com.application.nodawallet.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricManager
import com.application.nodawallet.R
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.FingerPrintHelper
import com.application.nodawallet.utils.UtilsDefault
import kotlinx.android.synthetic.main.activity_pincode.*
import kotlinx.android.synthetic.main.fragment_pincode.clearImg1
import kotlinx.android.synthetic.main.fragment_pincode.code1
import kotlinx.android.synthetic.main.fragment_pincode.code2
import kotlinx.android.synthetic.main.fragment_pincode.code3
import kotlinx.android.synthetic.main.fragment_pincode.code4
import kotlinx.android.synthetic.main.fragment_pincode.t0
import kotlinx.android.synthetic.main.fragment_pincode.t1
import kotlinx.android.synthetic.main.fragment_pincode.t2
import kotlinx.android.synthetic.main.fragment_pincode.t3
import kotlinx.android.synthetic.main.fragment_pincode.t4
import kotlinx.android.synthetic.main.fragment_pincode.t5
import kotlinx.android.synthetic.main.fragment_pincode.t6
import kotlinx.android.synthetic.main.fragment_pincode.t7
import kotlinx.android.synthetic.main.fragment_pincode.t8
import kotlinx.android.synthetic.main.fragment_pincode.t9
import kotlinx.android.synthetic.main.fragment_pincode.textCancel
import kotlinx.android.synthetic.main.fragment_pincode.tvEnterPin

class PincodeActivity : BaseActivity() {

    var isFromSplash = false
    private var isConfirm = false
    private var count = 0
    private var builder: StringBuilder? = null
    private var confirmBuilder: StringBuilder? = null
    var setPinTo = 1
    var oldpin = ""
    var newpin = ""

    companion object {
        lateinit var biometricManager: BiometricManager
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pincode)

        biometricManager = BiometricManager.from(this)

        val intent = intent
        if (intent!=null){
            isFromSplash =  intent.getBooleanExtra("isFromSplash",false)
        }

        builder = StringBuilder()
        confirmBuilder = StringBuilder()

        if (!isFromSplash){
            if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE){
                imgFingerprint.visibility = View.INVISIBLE
                Toast.makeText(this,getString(R.string.fingerprint_notfound),Toast.LENGTH_SHORT).show()
            }
            else {
                imgFingerprint.visibility = View.INVISIBLE
            }

        }
        else {
            if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE){
                imgFingerprint.visibility = View.INVISIBLE
            }
            else {
                imgFingerprint.visibility = View.VISIBLE
                fingerPrintAuth()
            }
        }

        imgFingerprint.setOnClickListener {
            fingerPrintAuth()

        }


        if (isFromSplash){
            setPinTo = 3
        }

        t0.setOnClickListener {

            if (isConfirm == false) {
                builder!!.append("0")
            } else {
                confirmBuilder!!.append("0")
            }
            count++
            updateimage()
        }

        t1.setOnClickListener {
            if (isConfirm == false) {
                builder!!.append("1")
            } else {
                confirmBuilder!!.append("1")
            }
            count++
            updateimage()
        }
        t2.setOnClickListener {
            if (isConfirm == false) {
                builder!!.append("2")
            } else {
                confirmBuilder!!.append("2")
            }
            count++
            updateimage()
        }

        t3.setOnClickListener {
            if (isConfirm == false) {
                builder!!.append("3")
            } else {
                confirmBuilder!!.append("3")
            }
            count++
            updateimage()
        }
        t4.setOnClickListener {
            if (isConfirm == false) {
                builder!!.append("4")
            } else {
                confirmBuilder!!.append("4")
            }
            count++
            updateimage()
        }
        t5.setOnClickListener {
            if (isConfirm == false) {
                builder!!.append("5")
            } else {
                confirmBuilder!!.append("5")
            }
            count++
            updateimage()
        }
        t6.setOnClickListener {
            if (isConfirm == false) {
                builder!!.append("6")
            } else {
                confirmBuilder!!.append("6")
            }
            count++
            updateimage()
        }
        t7.setOnClickListener {
            if (isConfirm == false) {
                builder!!.append("7")
            } else {
                confirmBuilder!!.append("7")
            }
            count++
            updateimage()
        }
        t8.setOnClickListener {
            if (isConfirm == false) {
                builder!!.append("8")
            } else {
                confirmBuilder!!.append("8")
            }
            count++
            updateimage()
        }
        t9.setOnClickListener {
            if (isConfirm == false) {
                builder!!.append("9")
            } else {
                confirmBuilder!!.append("9")
            }
            count++
            updateimage()
        }

        clearImg1.setOnClickListener {
            if (isConfirm == false) {
                if (builder!!.length > 0) {
                    count--
                    builder!!.setLength(builder!!.length - 1)
                } else {
                    count = 0
                }
            } else {
                if (confirmBuilder!!.length > 0) {
                    count--
                    confirmBuilder!!.setLength(confirmBuilder!!.length - 1)
                } else {
                    count = 0
                }
            }
            updateimage()
        }

        textCancel.setOnClickListener {
            finish()
        }


    }

    fun updateimage() {

        if (count == 0) {
            code1.setImageResource(R.drawable.passcode_unselect)
            code2.setImageResource(R.drawable.passcode_unselect)
            code3.setImageResource(R.drawable.passcode_unselect)
            code4.setImageResource(R.drawable.passcode_unselect)
        } else if (count == 1) {
            code1.setImageResource(R.drawable.passcode_select)
            code2.setImageResource(R.drawable.passcode_unselect)
            code3.setImageResource(R.drawable.passcode_unselect)
            code4.setImageResource(R.drawable.passcode_unselect)
        } else if (count == 2) {
            code1.setImageResource(R.drawable.passcode_select)
            code2.setImageResource(R.drawable.passcode_select)
            code3.setImageResource(R.drawable.passcode_unselect)
            code4.setImageResource(R.drawable.passcode_unselect)
        } else if (count == 3) {
            code1.setImageResource(R.drawable.passcode_select)
            code2.setImageResource(R.drawable.passcode_select)
            code3.setImageResource(R.drawable.passcode_select)
            code4.setImageResource(R.drawable.passcode_unselect)
        } else if (count == 4) {
            code1.setImageResource(R.drawable.passcode_select)
            code2.setImageResource(R.drawable.passcode_select)
            code3.setImageResource(R.drawable.passcode_select)
            code4.setImageResource(R.drawable.passcode_select)

            val handler = Handler()
            handler.postDelayed(Runnable {
                //Do something after 100ms
                validatePin()

            }, 100)

        }
    }

    private fun validatePin() {
        if (setPinTo == 1) {
            setPinTo = 2
            tvEnterPin.text = getString(R.string.confirmpin)
            newpin = builder.toString()
            builder!!.setLength(0)
            count = 0
            clearPins()
            isConfirm = false
        } else if (setPinTo == 2) {

            if (!newpin.equals(builder.toString())) {

                clearPins()
                builder!!.setLength(0)
                count = 0
                isConfirm = false
                Toast.makeText(this, getString(R.string.pinmismatch), Toast.LENGTH_SHORT).show()

            } else {

                // clearPins()
                UtilsDefault.updateSharedPreferenceString(Constants.PASSCODE,newpin)
                checkFingerPrint()


            }
        }
        else if (setPinTo == 3) {

            if (!UtilsDefault.getSharedPreferenceString(Constants.PASSCODE).equals(builder.toString())) {

                clearPins()
                builder!!.setLength(0)
                count = 0
                isConfirm = false
                Toast.makeText(this, getString(R.string.pinwrong), Toast.LENGTH_SHORT).show()

            } else {

                startActivity(Intent(this,MainActivity::class.java))
                finish()


            }
        }
    }

    private fun clearPins() {
        code1.setImageResource(R.drawable.passcode_unselect)
        code2.setImageResource(R.drawable.passcode_unselect)
        code3.setImageResource(R.drawable.passcode_unselect)
        code4.setImageResource(R.drawable.passcode_unselect)
    }

    private fun fingerPrintAuth(){
        FingerPrintHelper().scanData(this, object : FingerPrintHelper.FingerPrintAction {
            override fun onSuccess(result: String?) {

                if (UtilsDefault.getSharedPreferenceString(Constants.FINGERPRINT) == "yes") {
                    val intent = Intent(this@PincodeActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    UtilsDefault.updateSharedPreferenceString(Constants.FINGERPRINT, "yes")
                    finish()
                }


            }

            override fun onFailed(result: String?) {
                Toast.makeText(this@PincodeActivity, result, Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun checkFingerPrint() {

        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE){
            UtilsDefault.updateSharedPreferenceString(Constants.FINGERPRINT, "yes")

            finish()
        }
        else{

            FingerPrintHelper().scanData(this, object : FingerPrintHelper.FingerPrintAction {
                override fun onSuccess(result: String?) {

                    if (UtilsDefault.getSharedPreferenceString(Constants.FINGERPRINT) == "yes") {
                        val intent = Intent(this@PincodeActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        UtilsDefault.updateSharedPreferenceString(Constants.FINGERPRINT, "yes")
                        finish()
                    }

                }

                override fun onFailed(result: String?) {
                    clearPins()
                    builder!!.setLength(0)
                    count = 0
                    isConfirm = false
                    Toast.makeText(this@PincodeActivity, result, Toast.LENGTH_SHORT).show()
                }

            })

        }

    }



}

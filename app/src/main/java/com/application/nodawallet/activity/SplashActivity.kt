package com.application.nodawallet.activity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import com.application.nodawallet.R
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.FingerPrintHelper
import com.application.nodawallet.utils.UtilsDefault


class SplashActivity : AppCompatActivity() {

    companion object {
        lateinit var biometricManager: BiometricManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        biometricManager = BiometricManager.from(this)


        if (UtilsDefault.getSharedPreferenceInt(Constants.THEME) == 1){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val w = window
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        Thread(Runnable {
            Thread.sleep(3000)


            if (UtilsDefault.getSharedPreferenceString(Constants.FINGERPRINT) == "yes"){

                val intent =  Intent(this,PincodeActivity::class.java)
                intent.putExtra("isFromSplash",true)
                startActivity(intent)
                finish()

            }
            else {
                if (UtilsDefault.getSharedPreferenceInt(Constants.CODE_VERIFY) == 1){
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    val intent = Intent(this,PatternActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }



        }).start()


    }
}

package com.application.nodawallet.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.activity.MainActivity.Companion.mainBack
import com.application.nodawallet.activity.PincodeActivity
import com.application.nodawallet.activity.WalletConnectActivity
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.model.SocialResponse
import com.application.nodawallet.retrofit.NodaWalletAPI
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.PermissionUtils
import com.application.nodawallet.utils.UtilsDefault
import com.application.nodawallet.viewmodel.MultiCoinViewModel
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.layout_header.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class SettingsFragment : BaseFragment(R.layout.fragment_settings) {
    var mSocialresponse: SocialResponse? = null
    var twitterlink = ""
    var telegramlink = ""
    var PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
    )


    @Inject
    lateinit var nodaWalletAPI: NodaWalletAPI

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NodaApplication.instance.mComponent.inject(this)

        switchTheme.isChecked = UtilsDefault.getSharedPreferenceInt(Constants.THEME) == 1

        if (UtilsDefault.getSharedPreferenceString(Constants.PUSHNOTIFICATION)!=null){
            switchNotifications.isChecked = UtilsDefault.getSharedPreferenceString(Constants.PUSHNOTIFICATION) == "enable"
        }

        switchTheme.setOnCheckedChangeListener { button, _ ->
            MainActivity.bottomNav.menu.findItem(R.id.wallet).isChecked = true
            if (button!!.isChecked) {
                UtilsDefault.updateSharedPreferenceInt(Constants.THEME, 1)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            } else {
                UtilsDefault.updateSharedPreferenceInt(Constants.THEME, 2)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        textHeader.text = resources.getString(R.string.settings)


        switchTouchId.setOnCheckedChangeListener { button, _ ->

            if (button.isChecked) {
                val intent = Intent(activity, PincodeActivity::class.java)
                startActivity(intent)
            } else {
                UtilsDefault.updateSharedPreferenceString(Constants.FINGERPRINT, "no")
                Toast.makeText(activity, getString(R.string.fingerprint_disabled), Toast.LENGTH_SHORT).show()
            }

        }

        switchNotifications.setOnCheckedChangeListener { button, _ ->

            if (button.isChecked) {
                UtilsDefault.updateSharedPreferenceString(Constants.PUSHNOTIFICATION, "enable")
                Toast.makeText(activity, getString(R.string.notification_enabled), Toast.LENGTH_SHORT).show()


            } else {
                UtilsDefault.updateSharedPreferenceString(Constants.PUSHNOTIFICATION, "disable")
                Toast.makeText(activity, getString(R.string.notification_disabled), Toast.LENGTH_SHORT).show()
            }

        }

        rlWalletConnect?.setOnClickListener {

            val cointype2 = UtilsDefault.getSharedPreferenceString(Constants.COINTYPE)
            if (cointype2 == "public"){
                Toast.makeText(activity,getString(R.string.transaction_publicaddress), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            PermissionUtils().permissionList(
                PERMISSIONS,
                activity!!,
                object : PermissionUtils.PermissionCheck {
                    override fun onSuccess(result: String?) {
                        startActivity(Intent(activity, WalletConnectActivity::class.java))
                       // MainActivity.bottomNav.menu.findItem(R.id.scan).isChecked = true
                    }

                })

        }

        rlSettings.setOnClickListener {
            (context as MainActivity).push(MainWalletFragment())
        }

        rlAbout.setOnClickListener {
            (context as MainActivity).push(AboutFragment())

        }

        rlLanguage.setOnClickListener {
            (context as MainActivity).push(LanguageFragment())
        }

        rlNews.setOnClickListener {
         //   (context as MainActivity).push(NewsFragment())
        }

        rlAdvancedSettings.setOnClickListener {
            (context as MainActivity).push(AdvancedSettingsFragment())
        }

        rlTransaction.setOnClickListener {
            (context as MainActivity).push(TransHistoryFragment())
        }

        rlCurrency.setOnClickListener {
            (context as MainActivity).push(CurrencyFragment())
        }

        rlBrowser.setOnClickListener {
            // (context as MainActivity).push(BrowserFragment())
        }

        rlFaq.setOnClickListener {
            (context as MainActivity).push(FAQFragment())
        }

        rlTwitterbtn.setOnClickListener {
            if (twitterlink != "") {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(twitterlink)
                startActivity(i)
            }
        }

        rlTelegrambtn.setOnClickListener {
            if (telegramlink != "") {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(telegramlink)
                startActivity(i)
            }

        }
          getSociallink()
    }

    private fun getSociallink() {

        if (UtilsDefault.isOnline()) {

            nodaWalletAPI.getsocial().enqueue(object : Callback<SocialResponse> {
                override fun onFailure(call: Call<SocialResponse>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<SocialResponse>,
                    response: Response<SocialResponse>
                ) {

                    mSocialresponse = response.body()
                    if (mSocialresponse != null) {
                        if (mSocialresponse?.status == 1) {

                            twitterlink = mSocialresponse?.data?.twitter!!
                            telegramlink = mSocialresponse?.data?.telegram!!


                        }
                    }
                }

            })

        }

    }


    override fun onResume() {
        super.onResume()

        mainBack = 1
        switchTouchId.isChecked = UtilsDefault.getSharedPreferenceString(Constants.FINGERPRINT) == "yes"

    }

}

package com.application.nodawallet.activity

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import com.application.nodawallet.R
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.model.CmsResponse
import com.application.nodawallet.model.InputParams
import com.application.nodawallet.retrofit.NodaWalletAPI
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault
import kotlinx.android.synthetic.main.activity_about.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class AboutActivity : BaseActivity() {

    var header = ""
    var request = ""

    @Inject
    lateinit var nodaWalletAPI: NodaWalletAPI
    var cmsResponse: CmsResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        NodaApplication.instance.mComponent.inject(this)


        val intent = intent
        if (intent != null) {
            header = intent.getStringExtra("data") ?: ""
            request = intent.getStringExtra("request") ?: ""
        }

        textHeader?.text = header

        getContent()


        imgBackAbout.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getContent() {
        if (UtilsDefault.isOnline()) {

            showProgress()

            val inputParams = InputParams()
            inputParams.page = request
            inputParams.method = "cms_page"

            nodaWalletAPI.getcms(inputParams).enqueue(object : Callback<CmsResponse> {
                override fun onFailure(call: Call<CmsResponse>, t: Throwable) {
                    hideProgress()
                }

                override fun onResponse(
                    call: Call<CmsResponse>,
                    response: Response<CmsResponse>
                ) {
                    hideProgress()
                    cmsResponse = response.body()

                    if (cmsResponse != null && cmsResponse?.status!!) {

                        if (UtilsDefault.getSharedPreferenceString(Constants.LANGUAGE) == "en") {
                            val data = cmsResponse?.message!![0].description
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                webViewAbout?.text = Html.fromHtml(data, Html.FROM_HTML_MODE_LEGACY)

                            } else {
                                webViewAbout?.text = Html.fromHtml(data)
                            }
                        } else {
                            val data = cmsResponse?.message!![0].description_rs
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                webViewAbout?.text = Html.fromHtml(data, Html.FROM_HTML_MODE_LEGACY)

                            } else {
                                webViewAbout?.text = Html.fromHtml(data)
                            }

                        }
                    }
                }
            })
        } else {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }
}

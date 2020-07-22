package com.application.nodawallet.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.nodawallet.R
import com.application.nodawallet.adapter.AddTokenAdapter
import com.application.nodawallet.adapter.NetworkTokenAdapter
import com.application.nodawallet.model.ImportTokenModel
import kotlinx.android.synthetic.main.activity_networktoken.*

class NetworktokenActivity : BaseActivity() {

    private var tokenAdapter: NetworkTokenAdapter? = null
    val tokenList =  ArrayList<ImportTokenModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_networktoken)

        AddToken()

        mNetworkToken.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tokenAdapter = NetworkTokenAdapter(this,tokenList)
        mNetworkToken!!.adapter = tokenAdapter

        imgBackNwToken.setOnClickListener {
            onBackPressed()
        }


    }

    private fun AddToken() {
        tokenList.clear()
        tokenList.add(ImportTokenModel(0,"Ethereum","ETH",R.drawable.ic_eth))

    }

    override fun onBackPressed() {
        super.onBackPressed()

    }
}

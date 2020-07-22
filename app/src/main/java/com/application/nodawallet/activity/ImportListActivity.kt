package com.application.nodawallet.activity

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.nodawallet.R
import com.application.nodawallet.adapter.NetworkTokenAdapter
import com.application.nodawallet.model.ImportTokenModel
import kotlinx.android.synthetic.main.activity_import_list.*

class ImportListActivity : BaseActivity() {

    private var tokenAdapter: NetworkTokenAdapter? = null
    val tokenList =  ArrayList<ImportTokenModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import_list)


        AddToken()

        recycleImport.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tokenAdapter = NetworkTokenAdapter(this,tokenList)
        recycleImport!!.adapter = tokenAdapter

        tokenAdapter!!.onItemClick = { pos, _ ->
            val model = tokenList[pos]
            val intent =  Intent(this,SingleWalletImportActivity::class.java)
            intent.putExtra("tokenName",model.tokenName)
            intent.putExtra("tokenId",model.tokenId)
            startActivity(intent)
        }


        imgBackImport.setOnClickListener {
            onBackPressed()
        }

        constraintLayout1.setOnClickListener {
            val intent = Intent(this, MultiWalletImportActivity::class.java)
            startActivity(intent)
        }
    }

    private fun AddToken() {
        tokenList.clear()
        tokenList.add(ImportTokenModel(0,"Ethereum","ETH",R.drawable.ic_eth))

    }

}

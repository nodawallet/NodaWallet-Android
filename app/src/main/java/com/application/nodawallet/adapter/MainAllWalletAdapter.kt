package com.application.nodawallet.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.fragment.AdvancedSettingsFragment
import com.application.nodawallet.fragment.HomeFragment
import com.application.nodawallet.model.AllWalletList
import com.application.nodawallet.model.MainAdapterList
import com.application.nodawallet.model.MultiList
import com.application.nodawallet.model.MultiWalletList
import com.application.nodawallet.utils.*
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import org.web3j.utils.Numeric

class MainAllWalletAdapter(val context: Context,var list: ArrayList<MainAdapterList>) :
    RecyclerView.Adapter<MainAllWalletAdapter.ViewHolder>() {

    val TYPE_HEADER = 0
    val TYPE_ITEM = 1


    var onItemClick: ((pos: Int, view: View) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainAllWalletAdapter.ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        val inflatedView : View = when (viewType) {
            TYPE_HEADER -> layoutInflater.inflate(R.layout.item_header, parent,false)
            else -> layoutInflater.inflate(R.layout.item_main_allwallet, parent,false)
        }
        return ViewHolder(inflatedView)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {

        if (list[position].isHeader){
            return TYPE_HEADER
        }
        else {
            return TYPE_ITEM
        }

    }

    override fun onBindViewHolder(holder: MainAllWalletAdapter.ViewHolder, position: Int) {

        var model = list[position]

        if (model.isHeader){
            model.headerTitle.let { holder.setHeader(model.headerTitle!!) }
        }
        else {
            holder.setItems(model.walletList!!,context)
        }


        /*holder.textHeader.text = model.walletheader

        val coinId = UtilsDefault.getSharedPreferenceInt(Constants.COINID)


        if (model.walletId == coinId){
            holder.imgCheck.visibility = View.VISIBLE
        }
        else {
           holder.imgCheck.visibility = View.GONE
        }

        if (model.walletheader ==""){
            holder.consWallet.visibility = View.VISIBLE
        }
        else {
            holder.consWallet.visibility = View.GONE
        }

        if (model.walletType == 0) {
            holder.walletType.text = context.getString(R.string.multi_coin_wallet)
            holder.walletName.text = model.walletName

        } else if (model.walletType == 1) {
            try {
                val mnemonic = model.phrase
                val derivationPath = intArrayOf(44 or Bip32ECKeyPair.HARDENED_BIT, 60 or Bip32ECKeyPair.HARDENED_BIT, 0 or Bip32ECKeyPair.HARDENED_BIT, 0,0)
                //  val credentials =  WalletUtils.loadBip39Credentials("",mnemonic)
                val masterkeypair =  Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic,""))
                val derivedKeyPair =  Bip32ECKeyPair.deriveKeyPair(masterkeypair,derivationPath)
                var credential = Credentials.create(derivedKeyPair)
                var address = credential.address

                holder.walletType.text = address
                holder.walletName.text = model.walletName
            }
            catch (e:Exception){
                e.printStackTrace()
            }


        } else if (model.walletType == 2) {
            holder.walletType.text = model.phrase
            holder.walletName.text = model.walletName

        }

        try {
            if (model.walletImage!=null){
                holder.img1.setImageResource(model.walletImage)
            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }


        holder.imgInfo.setOnClickListener {

            val bundle = Bundle()
            val fragment = AdvancedSettingsFragment()
            bundle.putInt("walletId",model.walletId)
            fragment.arguments = bundle
            (context as MainActivity).push(fragment)
        }*/


        /*holder.consWallet.setOnClickListener {
            onItemClick?.invoke(position,it)
        }*/


    }


    internal fun setWallets(words: ArrayList<MainAdapterList>) {
        this.list = words
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textMyHeader = itemView.findViewById<CustomTextViewNormal>(R.id.textMyHeader)
        val imgInfo = itemView.findViewById<ImageView>(R.id.imgInfo)
        val imgCheck = itemView.findViewById<ImageView>(R.id.imgCheck)
        val img1 = itemView.findViewById<ImageView>(R.id.img1)
        val consWallet = itemView.findViewById<ConstraintLayout>(R.id.consWallet)
        var walletName = itemView.findViewById<CustomTextViewLight>(R.id.walletName)
        var walletType = itemView.findViewById<CustomTextViewLight>(R.id.walletType)
        var textTitle = itemView.findViewById<CustomTextViewSemiBold>(R.id.textTitle)

        fun setHeader(title:String){
            textMyHeader.text = title

        }

        fun setItems(walletList: MultiList, context: Context){
            walletName.text = walletList.walletName

            val coinId = UtilsDefault.getSharedPreferenceInt(Constants.COINID)

            if (walletList.walletId == coinId){
                imgCheck.visibility = View.VISIBLE
            }
            else {
                imgCheck.visibility = View.GONE
            }

            walletName.setOnClickListener {
                Log.d("walletname",walletList.walletName)
            }

            if (walletList.walletType == 0) {
                walletType.text = context.getString(R.string.multi_coin_wallet)
                walletName.text = walletList.walletName

            } else if (walletList.walletType == 1) {
                try {
                    val mnemonic = walletList.phrase
                    val credentials = Credentials.create(mnemonic)

                    walletType.text = credentials.address
                    walletName.text = walletList.walletName
                }
                catch (e:Exception){
                    e.printStackTrace()
                }


            } else if (walletList.walletType == 2) {
                walletType.text = walletList.phrase
                walletName.text = walletList.walletName

            }

            try {
                if (walletList.walletImage!=null){
                    img1.setImageResource(walletList.walletImage!!)
                }
            }
            catch (e:Exception){
                e.printStackTrace()
            }

            imgInfo.setOnClickListener {

                val bundle = Bundle()
                val fragment = AdvancedSettingsFragment()
                bundle.putInt("walletId",walletList.walletId)
                fragment.arguments = bundle
                (context as MainActivity).push(fragment)
            }
            consWallet.setOnClickListener {
                val coinId = walletList.walletId
                UtilsDefault.updateSharedPreferenceString(Constants.WALLETNAME,walletList.walletName)
                UtilsDefault.updateSharedPreferenceInt(Constants.COINID,coinId)
                (context as MainActivity).push(HomeFragment())
            }
        }


    }
}
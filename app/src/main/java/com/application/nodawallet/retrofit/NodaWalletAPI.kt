package com.application.nodawallet.retrofit

import com.application.nodawallet.model.*
import com.binance.dex.api.client.domain.Fees
import com.binance.dex.api.client.domain.TransactionMetadata
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface NodaWalletAPI {


    @POST("apiv2/create_transaction")
    fun exchangebtc(@Body inputParams: InputParams): Call<BTCExchangeResponse>

    @POST("apiv2/get_min_transaction")
    fun getminimum(@Body inputParams: InputParams): Call<MinimumBtcResponse>

    @POST("apiv2/get_approx_amount")
    fun getreceiveaamount(@Body inputParams: InputParams): Call<MinimumBtcResponse>

    @POST("api/get_static_page")
    fun getcms(@Body inputParams: InputParams): Call<CmsResponse>

    @GET
    fun getbalance(@Url url: String): Call<BalanceResponse>

    @GET
    fun getethexplorer(@Url url: String): Call<ETHExplorer>


    @GET
    fun gethistory(@Url url: String): Call<TransactionHistoryResponse>

    @GET
    fun getbtchistory(@Url url: String): Call<BtcHistoryResponse>

    @GET
    fun getmarketPrice(@Url url: String): Call<List<MarketResponse>>

    @GET
    fun gethash(@Url url: String): Call<HashResponse>

    @GET
    fun getbtcfees(@Url url: String): Call<BtcFees>

    @GET
    fun contractverify(@Url url: String): Call<ContractVerify>

    @GET
    fun tokeninfo(@Url url: String): Call<TokenInformation>

    @GET
    fun coinlist(@Url url: String): Call<List<CoinListResponse>>

    @GET("api/social")
    fun getsocial(): Call<SocialResponse>

    @GET("api/getfaq")
    fun getFaq(): Call<FAQResponse>

    @GET("apiv2/get_currency")
    fun getcurrency(): Call<CurrencyResponse>

    @GET("api/getNews")
    fun getnews(): Call<NewsResponse>

    @GET
    fun getmarketchart(@Url url: String): Call<MarketChartResponse>

}
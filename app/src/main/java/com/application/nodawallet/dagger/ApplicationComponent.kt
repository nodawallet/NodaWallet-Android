package com.application.nodawallet.dagger

import com.application.nodawallet.activity.*
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.fragment.*
import com.application.nodawallet.retrofit.RetrofitModule
import com.application.nodawallet.walletconnect.WCClient
import dagger.Component
import retrofit2.Retrofit
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class, RetrofitModule::class])
interface ApplicationComponent {

    fun inject(application: NodaApplication)
    fun retrofit(): Retrofit
    fun inject(tokenFragment: TokenFragment) {

    }

    fun inject(SettingsFragment: SettingsFragment) {

    }

    fun inject(currencyDetailFragment: CurrencyDetailFragment) {

    }

    fun inject(faqFragment: FAQFragment) {

    }

    fun inject(exchangeFragment: ExchangeFragment) {

    }

    fun inject(addTokenFragment: AddTokenFragment) {

    }

    fun inject(newsFragment: NewsFragment) {

    }

    fun inject(confirmDepositActivity: ConfirmDepositActivity) {

    }

    fun inject(investmentFragment: InvestmentFragment) {

    }

    fun inject(exchangeHistoryFragment: ExchangeHistoryFragment) {

    }

    fun inject(sendActivity: SendActivity) {

    }

    fun inject(aboutActivity: AboutActivity) {

    }


    fun inject(customTokenActivity: CustomTokenActivity) {

    }

    fun inject(wcClient: WCClient) {

    }

    fun inject(multiWalletImportActivity: MultiWalletImportActivity) {

    }

    fun inject(verifyPatternFragment: VerifyPatternFragment) {

    }

    fun inject(transDetailsActivity: TransDetailsActivity) {

    }
}
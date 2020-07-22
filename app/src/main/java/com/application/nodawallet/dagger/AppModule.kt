package com.application.nodawallet.dagger

import android.content.Context
import com.application.nodawallet.application.NodaApplication
import com.application.nodawallet.retrofit.NodaWalletAPI
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
class AppModule(private val app:NodaApplication) {

    @Provides
    @Singleton
    fun provideContext(): Context = app

    @Provides
    fun provideNodaWalletInterface(retrofit: Retrofit): NodaWalletAPI {
        return retrofit.create<NodaWalletAPI>(NodaWalletAPI::class.java)
    }


}
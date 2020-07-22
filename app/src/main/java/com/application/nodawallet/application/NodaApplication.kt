package com.application.nodawallet.application

import android.content.Context
import android.content.res.Resources
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.application.nodawallet.BuildConfig
import com.application.nodawallet.R
import com.application.nodawallet.dagger.AppModule
import com.application.nodawallet.dagger.ApplicationComponent
import com.application.nodawallet.dagger.DaggerApplicationComponent
import com.application.nodawallet.retrofit.RetrofitModule
import com.application.nodawallet.utils.LocaleHelper
import com.google.firebase.FirebaseApp

class NodaApplication : MultiDexApplication() {

    init {
        instance = this
    }

    lateinit var mComponent: ApplicationComponent

    companion object {
        lateinit var instance: NodaApplication
            private set
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        mComponent = DaggerApplicationComponent.builder()
            .appModule(AppModule(this))
            .retrofitModule(RetrofitModule(instance, BuildConfig.BASE_URL))
            .build()
        mComponent.inject(this)

        FirebaseApp.initializeApp(this)
        MultiDex.install(this)

    }

    fun getComponent(): ApplicationComponent {
        return mComponent
    }

    fun getAppResources(): Resources {
        return instance.resources
    }

    fun getAppString(resourceId: Int): String {
        return getAppResources().getString(resourceId)
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    fun getGlobalContext(): Context? {
        return instance
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper().onAttach(base, "en"))
    }


}
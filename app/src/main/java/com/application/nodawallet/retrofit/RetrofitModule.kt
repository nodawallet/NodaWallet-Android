package com.application.nodawallet.retrofit

import android.content.Context
import android.util.Log
import com.application.nodawallet.BuildConfig
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.internal.http2.Http2
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class RetrofitModule(val context : Context?, val  baseUrl:String) {

    @Provides
    @Singleton
    internal fun provideInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            var request: Request? = null
            try {

                if (UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN) != null) {
                    var token = ""
                    if (UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN)!!.contains("Bearer")) {
                        token = UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN)!!
                    } else {
                        token ="Bearer " + UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN)
                    }
                    request = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("authorization", token)
                        .method(original.method, original.body)
                        .build()
                    Log.d("authorization", "intercept: " + "Bearer "
                            + UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN))
                } else {
                    request = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .method(original.method, original.body)
                        .build()
                    Log.d(
                        "authorization", "intercept: " + "Bearer "
                                + UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN)
                    )
                }


            } catch (authFailureError: Exception) {
                authFailureError.printStackTrace()
            }

            chain.proceed(request!!)
        }
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    internal fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {

        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder.interceptors().add(interceptor)

        okHttpBuilder.readTimeout(60, TimeUnit.SECONDS)
        okHttpBuilder.connectTimeout(60, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            okHttpBuilder.interceptors().add(logging)
        }


        return okHttpBuilder.build()
    }

    @Provides
    @Singleton
    internal fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()
    }
}
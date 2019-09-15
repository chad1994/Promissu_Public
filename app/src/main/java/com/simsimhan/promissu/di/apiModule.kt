package com.simsimhan.promissu.di

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import com.simsimhan.promissu.BuildConfig
import com.simsimhan.promissu.network.AuthAPI
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

val apiModule = module {

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        } as Interceptor
    }

    single {
        OkHttpClient.Builder()
                .addInterceptor(get())
                .addNetworkInterceptor(StethoInterceptor())
                .build()
    }

    single<AuthAPI> {
        Retrofit.Builder()
                .client(get())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(
                        GsonConverterFactory.create(
                                GsonBuilder()
                                        .setLenient()
                                        .create()
                        )
                )
                .baseUrl(BuildConfig.SERVER_URL)
                .build()
                .create()
    }

}
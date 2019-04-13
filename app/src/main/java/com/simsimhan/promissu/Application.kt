package com.simsimhan.promissu

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.GsonBuilder
import com.kakao.auth.*
import com.naver.maps.map.NaverMapSdk
import com.simsimhan.promissu.cache.DiskCache
import com.simsimhan.promissu.di.appModules
import io.branch.referral.Branch
import io.branch.referral.BranchUtil
import io.palaima.debugdrawer.timber.data.LumberYard
import io.requery.sql.EntityDataStore
import net.danlew.android.joda.JodaTimeAndroid
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.android.startKoin
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class PromissuApplication : MultiDexApplication() {

    private val httpClient: OkHttpClient
        get() {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val builder = OkHttpClient.Builder()
            return builder.addInterceptor(httpLoggingInterceptor)
                    .addNetworkInterceptor(StethoInterceptor())
                    .build()
        }

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        instance = this
        KakaoSDK.init(KakaoSDKAdapter())

        if (BranchUtil.isTestModeEnabled(this)) {
            Branch.getTestInstance(this)
        } else {
            Branch.getInstance(this)
        }


        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }

        startKoin(this, appModules)
        JodaTimeAndroid.init(this)
        val lumberYard = LumberYard.getInstance(this)
        lumberYard.cleanUp()
        Timber.plant(lumberYard.tree())
        Timber.plant(Timber.DebugTree())

        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NaverClientId)
        //        DatabaseSource source = new DatabaseSource(this, Models.DEFAULT, 1);
        //        if (BuildConfig.DEBUG)
        //            source.setTableCreationMode(TableCreationMode.DROP_CREATE); // use this in development mode to drop and recreate the tables on every upgrade
        //        Configuration configuration = source.getConfiguration();
        //        dataStore = ReactiveSupport.toReactiveStore(new EntityDataStore<Persistable>(configuration));

        val gson = GsonBuilder()
                .setLenient()
                .create()

        retrofit = Retrofit.Builder()
                .client(httpClient)
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        daumRetrofit = Retrofit.Builder()
                .client(httpClient)
                .baseUrl(DAUM_API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        naverRetrofit = Retrofit.Builder()
                .client(httpClient)
                .baseUrl(NAVER_API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        diskCache = DiskCache(getSharedPreferences("USER_DISK_CACHE", Context.MODE_PRIVATE))

    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }

    private class KakaoSDKAdapter : KakaoAdapter() {
        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         *
         * @return Session의 설정값.
         */
        override fun getSessionConfig(): ISessionConfig {
            return object : ISessionConfig {
                override fun getAuthTypes(): Array<AuthType> {
                    return arrayOf(AuthType.KAKAO_TALK)
                }

                override fun isUsingWebviewTimer(): Boolean {
                    return false
                }

                override fun isSecureMode(): Boolean {
                    return false
                }

                override fun getApprovalType(): ApprovalType? {
                    return ApprovalType.INDIVIDUAL
                }

                override fun isSaveFormData(): Boolean {
                    return true
                }
            }
        }

        override fun getApplicationConfig(): IApplicationConfig {
            return IApplicationConfig { PromissuApplication.globalApplicationContext }
        }
    }

    companion object {
        /**
         * @return [EntityDataStore] single instance for the application.
         *
         *
         * Note if you're using Dagger you can make this part of your application level module returning
         * `@Provides @Singleton`.
         */
        //    public static ReactiveEntityStore<Persistable> getData() {
        //        return dataStore;
        //    }
        var retrofit: Retrofit? = null
            private set
        var daumRetrofit: Retrofit? = null
            private set
        var naverRetrofit: Retrofit? = null
            private set
        var diskCache: DiskCache? = null
            private set
        private var instance: PromissuApplication? = null
        private val DAUM_API_URL = "https://dapi.kakao.com/v2/"
        private val NAVER_API_URL = "https://naveropenapi.apigw.ntruss.com/map-place/v1/"

        //    private static ReactiveEntityStore<Persistable> dataStore;

        private val globalApplicationContext: Context
            get() {
                if (instance == null)
                    throw IllegalStateException("This Application does not inherit com.kakao.GlobalApplication")
                return instance as PromissuApplication
            }
    }

}

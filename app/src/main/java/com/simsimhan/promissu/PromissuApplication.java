package com.simsimhan.promissu;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.naver.maps.map.NaverMapSdk;
import com.simsimhan.promissu.cache.DiskCache;

import net.danlew.android.joda.JodaTimeAndroid;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import io.branch.referral.Branch;
import io.branch.referral.BranchUtil;
import io.palaima.debugdrawer.timber.data.LumberYard;
import io.requery.sql.EntityDataStore;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class PromissuApplication extends MultiDexApplication {
    private static Retrofit retrofit;
    private static Retrofit daumRetrofit;
    private static DiskCache diskCache;
    private static PromissuApplication instance;
    private static final String DAUM_API_URL = "https://dapi.kakao.com/v2/";

//    private static ReactiveEntityStore<Persistable> dataStore;

    private static Context getGlobalApplicationContext() {
        if (instance == null)
            throw new IllegalStateException("This Application does not inherit com.kakao.GlobalApplication");
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        instance = this;
        KakaoSDK.init(new KakaoSDKAdapter());

        if (BranchUtil.isTestModeEnabled(this)) {
            Branch.getTestInstance(this);
        } else {
            Branch.getInstance(this);
        }


        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

        JodaTimeAndroid.init(this);
        LumberYard lumberYard = LumberYard.getInstance(this);
        lumberYard.cleanUp();
        Timber.plant(lumberYard.tree());
        Timber.plant(new Timber.DebugTree());

        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NaverClientId));
//        DatabaseSource source = new DatabaseSource(this, Models.DEFAULT, 1);
//        if (BuildConfig.DEBUG)
//            source.setTableCreationMode(TableCreationMode.DROP_CREATE); // use this in development mode to drop and recreate the tables on every upgrade
//        Configuration configuration = source.getConfiguration();
//        dataStore = ReactiveSupport.toReactiveStore(new EntityDataStore<Persistable>(configuration));

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .client(getHttpClient())
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        daumRetrofit = new Retrofit.Builder()
                .client(getHttpClient())
                .baseUrl(DAUM_API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        diskCache = new DiskCache(getSharedPreferences("USER_DISK_CACHE", MODE_PRIVATE));
    }

    /**
     * @return {@link EntityDataStore} single instance for the application.
     * <p/>
     * Note if you're using Dagger you can make this part of your application level module returning
     * {@code @Provides @Singleton}.
     */
//    public static ReactiveEntityStore<Persistable> getData() {
//        return dataStore;
//    }
    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public static Retrofit getDaumRetrofit() {
        return daumRetrofit;
    }

    public static DiskCache getDiskCache() {
        return diskCache;
    }

    private OkHttpClient getHttpClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        return builder.addInterceptor(httpLoggingInterceptor)
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }

    private static class KakaoSDKAdapter extends KakaoAdapter {
        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         *
         * @return Session의 설정값.
         */
        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[]{AuthType.KAKAO_TALK};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig() {
            return () -> PromissuApplication.getGlobalApplicationContext();
        }
    }

}

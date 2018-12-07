package com.simsimhan.promissu;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.simsimhan.promissu.cache.DiskCache;

import net.danlew.android.joda.JodaTimeAndroid;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import io.palaima.debugdrawer.timber.data.LumberYard;
import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class PromissuApplication extends MultiDexApplication {
    private static Retrofit retrofit;
    private static DiskCache diskCache;
//    private static ReactiveEntityStore<Persistable> dataStore;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        Stetho.initializeWithDefaults(this);
        JodaTimeAndroid.init(this);
        LumberYard lumberYard = LumberYard.getInstance(this);
        lumberYard.cleanUp();
        Timber.plant(lumberYard.tree());
        Timber.plant(new Timber.DebugTree());

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


}

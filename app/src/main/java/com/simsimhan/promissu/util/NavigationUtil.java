package com.simsimhan.promissu.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;

import com.simsimhan.promissu.MainActivity;
import com.simsimhan.promissu.detail.PromiseDetailActivity;
import com.simsimhan.promissu.login.LoginActivity;
import com.simsimhan.promissu.map.MapSearchActivity;
import com.simsimhan.promissu.promise.create.CreatePromiseActivity;
import com.simsimhan.promissu.view.DummyTutorialActivity;

import androidx.appcompat.app.AppCompatActivity;

public class NavigationUtil {
    public static final int REQEUSET_LOGIN = 101;

    public static void replaceWithLoginView(AppCompatActivity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void openAddPromiseScreen(AppCompatActivity activity) {
        Intent intent = new Intent(activity, CreatePromiseActivity.class);
        activity.startActivity(intent);
    }

    public static void openMapScreen(AppCompatActivity activity) {
        Intent intent = new Intent(activity, MapSearchActivity.class);
        activity.startActivity(intent);
    }


    public static void openEmailQuery(AppCompatActivity appCompatActivity, DisplayMetrics displayMetrics, String userId, String title) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:yol-support@medibloc.org"));

        final String deviceModel = truncateAt(Build.MODEL, 20);
        final String deviceResolution = displayMetrics.heightPixels + "x" + displayMetrics.widthPixels;
        final String deviceDensity = displayMetrics.densityDpi + "dpi";
        final String deviceRelease = Build.VERSION.RELEASE;
        final String deviceApi = String.valueOf(Build.VERSION.SDK_INT);

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);

        String body = "[프라미슈] 어플리케이션 사용하며 겪은 문의사항을 아래 입력해주세요."
                + "\n\n 내용: \n\n\n\n"

                + "\n ================================="
                + "\n userID: " + userId
                + "\n deviceModel: " + deviceModel
                + "\n deviceResolution: " + deviceResolution
                + "\n deviceDensity: " + deviceDensity
                + "\n deviceRelease: " + deviceRelease
                + "\n deviceApi: " + deviceApi
                + "\n =================================";

        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
//        emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body); //If you are using HTML in your body text

        appCompatActivity.startActivity(Intent.createChooser(emailIntent, "이메일 문의하기"));
    }


    public static void replaceWithMainView(AppCompatActivity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

    private static String truncateAt(String string, int length) {
        return string.length() > length ? string.substring(0, length) : string;
    }

    public static void openPromiseDetilScreen(AppCompatActivity appCompatActivity) {
        Intent intent = new Intent(appCompatActivity, PromiseDetailActivity.class);
        appCompatActivity.startActivity(intent);
    }
}

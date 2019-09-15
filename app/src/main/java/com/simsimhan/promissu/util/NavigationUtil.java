//package com.simsimhan.promissu.util;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Build;
//import android.util.DisplayMetrics;
//import android.widget.Toast;
//
//import com.simsimhan.promissu.MainActivity;
//import com.simsimhan.promissu.network.model.PromiseResponse;
//import com.simsimhan.promissu.network.model.Promise;
//import com.simsimhan.promissu.ui.detail.PromiseDetailActivity;
//import com.simsimhan.promissu.ui.login.LoginActivity;
//import com.simsimhan.promissu.ui.map.LocationSearchActivity;
//import com.simsimhan.promissu.ui.pastdetail.DetailPastActivity;
//import com.simsimhan.promissu.ui.promise.create.CreateActivity;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class NavigationUtil {
//    public static final int REQEUSET_LOGIN = 101;
//    public static final int REQUEST_MAP_SEARCH = 102;
//    public static final int REQUEST_CREATE_PROMISE = 103;
//    public static final int REQUEST_MODIFY_PROMISE = 104;
//
//    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9991;
//
//    public enum PROMISE_STATUS {
//        // 현재 사람들을 초대중인 상태
//        INVITING(0),
//
//        // 사람들이 다 모이고, 약속이 시작되기를 기다리는 상태 -> (변경) 약속 시작 1시간 전
//        PENDING(1),
//
//        // 약속이 진행중인 상태
////        MEETING(2),
//
//        //약속 시간이 끝난 상태
//        CLOSED(2),
//
//        // 약속이 삭제된 상태
//        DELETED(-1);
//
//        int value;
//
//        PROMISE_STATUS(final int value) {
//            this.value = value;
//        }
//
//        public int getValue() {
//            return value;
//        }
//    }
//
//    public static void replaceWithLoginView(AppCompatActivity activity) {
//        Intent intent = new Intent(activity, LoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        activity.startActivity(intent);
//        activity.finish();
//    }
//
//    public static void openAddPromiseScreen(AppCompatActivity activity) {
////        Intent intent = new Intent(activity, CreatePromiseActivity.class);
//        Intent intent = new Intent(activity, CreateActivity.class);
//        activity.startActivityForResult(intent, REQUEST_CREATE_PROMISE);
//    }
//
//    public static void openModifyPromiseScreen(AppCompatActivity activity, Promise.Response promise) {
//        Intent intent = new Intent(activity, CreateActivity.class);
//        intent.putExtra("promise", promise);
//        activity.startActivityForResult(intent, REQUEST_MODIFY_PROMISE);
//    }
//
//    public static void openMapScreen(Activity activity) {
//        Intent intent = new Intent(activity, LocationSearchActivity.class);
//        activity.startActivityForResult(intent, REQUEST_MAP_SEARCH);
//    }
//
//
//    public static void openEmailQuery(AppCompatActivity appCompatActivity, DisplayMetrics displayMetrics, String userId, String title) {
//        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:yol-support@medibloc.org"));
//
//        final String deviceModel = truncateAt(Build.MODEL, 20);
//        final String deviceResolution = displayMetrics.heightPixels + "x" + displayMetrics.widthPixels;
//        final String deviceDensity = displayMetrics.densityDpi + "dpi";
//        final String deviceRelease = Build.VERSION.RELEASE;
//        final String deviceApi = String.valueOf(Build.VERSION.SDK_INT);
//
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
//
//        String body = "[프라미슈] 어플리케이션 사용하며 겪은 문의사항을 아래 입력해주세요."
//                + "\n\n 내용: \n\n\n\n"
//
//                + "\n ================================="
//                + "\n userID: " + userId
//                + "\n deviceModel: " + deviceModel
//                + "\n deviceResolution: " + deviceResolution
//                + "\n deviceDensity: " + deviceDensity
//                + "\n deviceRelease: " + deviceRelease
//                + "\n deviceApi: " + deviceApi
//                + "\n =================================";
//
//        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
////        emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body); //If you are using HTML in your body text
//
//        appCompatActivity.startActivity(Intent.createChooser(emailIntent, "이메일 문의하기"));
//    }
//
//
//    public static void replaceWithMainView(AppCompatActivity activity) {
//        Intent intent = new Intent(activity, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        activity.startActivity(intent);
//        activity.finish();
//    }
//
//    private static String truncateAt(String string, int length) {
//        return string.length() > length ? string.substring(0, length) : string;
//    }
//
//    public static void enterRoom(AppCompatActivity appCompatActivity, PromiseResponse promise, Boolean isPast) {
////        if (promise.getStatus() == PROMISE_STATUS.PENDING.getValue()) {
////            openPendingScreen(appCompatActivity, promise);
////        } else
//        if (promise.getPromise().getStatus() == PROMISE_STATUS.DELETED.getValue()) {
//            Toast.makeText(appCompatActivity, "삭제된 방입니다.", Toast.LENGTH_SHORT).show();
//        } else if (isPast) {
//            openPromiseDetailPastScreen(appCompatActivity, promise);
//        } else {
//            openPromiseDetailScreen(appCompatActivity, promise);
//        }
//    }
//
//    private static void openPromiseDetailScreen(AppCompatActivity appCompatActivity, PromiseResponse promise) {
//        Intent intent = new Intent(appCompatActivity, PromiseDetailActivity.class);
//        intent.putExtra("promise", promise.getPromise());
//        appCompatActivity.startActivity(intent);
//    }
//
//    private static void openPromiseDetailPastScreen(AppCompatActivity appCompatActivity, PromiseResponse promise) {
//        Intent intent = new Intent(appCompatActivity, DetailPastActivity.class);
//        intent.putExtra("promise", promise);
//        appCompatActivity.startActivity(intent);
//
//    }
//
//}

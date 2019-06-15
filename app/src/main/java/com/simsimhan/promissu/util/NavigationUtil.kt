//package com.simsimhan.promissu.util
//
//
//import android.app.Activity
//import android.content.Intent
//import android.net.Uri
//import android.os.Build
//import android.util.DisplayMetrics
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.simsimhan.promissu.MainActivity
//import com.simsimhan.promissu.ui.detail.PromiseDetailActivity
//import com.simsimhan.promissu.ui.login.LoginActivity
//import com.simsimhan.promissu.ui.map.LocationSearchActivity
//import com.simsimhan.promissu.network.model.Promise
//import com.simsimhan.promissu.ui.promise.PendingPromiseActivity
//import com.simsimhan.promissu.ui.promise.create.CreateActivity
//
//object NavigationUtil {
//
//    val REQEUSET_LOGIN = 101
//    val REQUEST_MAP_SEARCH = 102
//    val REQUEST_CREATE_PROMISE = 103
//
//    val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9991
//
//    enum class PROMISE_STATUS private constructor(value: Int) {
//        // 현재 사람들을 초대중인 상태
//        INVITING(0),
//
//        // 사람들이 다 모이고, 약속이 시작되기를 기다리는 상태 -> (변경) 약속 시작 1시간 전
//        PENDING(1),
//
//        // 약속 시간이 다 끝난 상태
//        CLOSED(2),
//
//        // 약속이 삭제된 상태
//        DELETED(-1);
//
//        var value: Int = 0
//            internal set
//
//        init {
//            this.value = value
//        }
//    }
//
//    fun replaceWithLoginView(activity: AppCompatActivity) {
//        val intent = Intent(activity, LoginActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        activity.startActivity(intent)
//        activity.finish()
//    }
//
//    fun openAddPromiseScreen(activity: AppCompatActivity) {
//        //        Intent intent = new Intent(activity, CreatePromiseActivity.class);
//        val intent = Intent(activity, CreateActivity::class.java)
//        activity.startActivityForResult(intent, REQUEST_CREATE_PROMISE)
//    }
//
//    fun openMapScreen(activity: Activity) {
//        val intent = Intent(activity, LocationSearchActivity::class.java)
//        activity.startActivityForResult(intent, REQUEST_MAP_SEARCH)
//    }
//
//
//    fun openEmailQuery(appCompatActivity: AppCompatActivity, displayMetrics: DisplayMetrics, userId: String, title: String) {
//        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:yol-support@medibloc.org"))
//
//        val deviceModel = truncateAt(Build.MODEL, 20)
//        val deviceResolution = displayMetrics.heightPixels.toString() + "x" + displayMetrics.widthPixels
//        val deviceDensity = displayMetrics.densityDpi.toString() + "dpi"
//        val deviceRelease = Build.VERSION.RELEASE
//        val deviceApi = Build.VERSION.SDK_INT.toString()
//
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, title)
//
//        val body = ("[프라미슈] 어플리케이션 사용하며 겪은 문의사항을 아래 입력해주세요."
//                + "\n\n 내용: \n\n\n\n"
//
//                + "\n ================================="
//                + "\n userID: " + userId
//                + "\n deviceModel: " + deviceModel
//                + "\n deviceResolution: " + deviceResolution
//                + "\n deviceDensity: " + deviceDensity
//                + "\n deviceRelease: " + deviceRelease
//                + "\n deviceApi: " + deviceApi
//                + "\n =================================")
//
//        emailIntent.putExtra(Intent.EXTRA_TEXT, body)
//        //        emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body); //If you are using HTML in your body text
//
//        appCompatActivity.startActivity(Intent.createChooser(emailIntent, "이메일 문의하기"))
//    }
//
//
//    fun replaceWithMainView(activity: AppCompatActivity) {
//        val intent = Intent(activity, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        activity.startActivity(intent)
//        activity.finish()
//    }
//
//    private fun truncateAt(string: String, length: Int): String {
//        return if (string.length > length) string.substring(0, length) else string
//    }
//
//    fun enterRoom(appCompatActivity: AppCompatActivity, promise: Promise.Response) {
//        //        if (promise.getStatus() == PROMISE_STATUS.PENDING.getValue()) {
//        //            openPendingScreen(appCompatActivity, promise);
//        //        } else
//        if (promise.status == PROMISE_STATUS.DELETED.value) {
//            Toast.makeText(appCompatActivity, "삭제된 방입니다.", Toast.LENGTH_SHORT).show()
//        } else {
//            openPromiseDetailScreen(appCompatActivity, promise)
//        }
//    }
//
//    private fun openPromiseDetailScreen(appCompatActivity: AppCompatActivity, promise: Promise.Response) {
//        val intent = Intent(appCompatActivity, PromiseDetailActivity::class.java)
//        intent.putExtra("promise", promise)
//        appCompatActivity.startActivity(intent)
//    }
//
//    private fun openPendingScreen(appCompatActivity: AppCompatActivity, promise: Promise.Response) {
//        val intent = Intent(appCompatActivity, PendingPromiseActivity::class.java)
//        intent.putExtra("promise", promise)
//        appCompatActivity.startActivity(intent)
//    }
//}

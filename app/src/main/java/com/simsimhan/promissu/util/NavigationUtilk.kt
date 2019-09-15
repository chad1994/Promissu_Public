package com.simsimhan.promissu.util

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.network.model.PromiseResponse
import com.simsimhan.promissu.ui.detail.PromiseDetailActivity
import com.simsimhan.promissu.ui.invinting.InvitingActivity
import com.simsimhan.promissu.ui.invitingList.InvitingListActivity
import com.simsimhan.promissu.ui.login.LoginActivity
import com.simsimhan.promissu.ui.main.MainActivityk
import com.simsimhan.promissu.ui.map.LocationMapActivity
import com.simsimhan.promissu.ui.pastList.PastListActivity
import com.simsimhan.promissu.ui.pastdetail.DetailPastActivity
import com.simsimhan.promissu.ui.pending.PendingActivity
import com.simsimhan.promissu.ui.promise.create.CreateActivity


class NavigationUtilk {

    companion object {
        const val REQEUSET_LOGIN = 101
        const val REQUEST_MAP_SEARCH = 102
        const val REQUEST_CREATE_PROMISE = 103
        const val REQUEST_MODIFY_PROMISE = 104
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9991


        fun replaceWithLoginView(activity: AppCompatActivity) {
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            activity.startActivity(intent)
            activity.finish()
        }

        fun openAddPromiseScreen(activity: AppCompatActivity) {
            val intent = Intent(activity, CreateActivity::class.java)
            activity.startActivityForResult(intent, REQUEST_CREATE_PROMISE)
        }

        fun openModifyPromiseScreen(activity: AppCompatActivity, promise: Promise.Response) {
            val intent = Intent(activity, CreateActivity::class.java)
            intent.putExtra("promise", promise)
            activity.startActivityForResult(intent, REQUEST_MODIFY_PROMISE)
        }

        fun replaceWithMainView(activity: AppCompatActivity) {
            val intent = Intent(activity, MainActivityk::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            activity.startActivity(intent)
            activity.finish()
        }

        fun openPastList(activity: AppCompatActivity){
            val intent = Intent(activity, PastListActivity::class.java)
            activity.startActivity(intent)
        }

        fun openInvitingList(activity: AppCompatActivity){
            val intent = Intent(activity,InvitingListActivity::class.java)
            activity.startActivity(intent)
        }

        fun enterRoom(appCompatActivity: AppCompatActivity, promise: PromiseResponse, isPast: Boolean?) {
            if (promise.promise.status == PromiseState.DELETED.value) {
                Toast.makeText(appCompatActivity, "삭제된 방입니다.", Toast.LENGTH_SHORT).show()
            } else if (isPast!!) {
                openPromiseDetailPastScreen(appCompatActivity, promise)
            } else {
                openPromiseDetailScreen(appCompatActivity, promise)
            }
        }

        fun enterInvitingRoomAfterCreateRoom(activity: AppCompatActivity, promise : Promise.Response){
            val intent = Intent(activity, InvitingActivity::class.java)
            intent.putExtra("promise",promise)
            activity.startActivity(intent)
            activity.finish()
        }

        fun enterInvitingRoomWithoutActivityFinish(activity: AppCompatActivity, promise: Promise.Response){
            val intent = Intent(activity, InvitingActivity::class.java)
            intent.putExtra("promise",promise)
            activity.startActivity(intent)
        }

        fun enterPendingRoom(activity: AppCompatActivity, promise: Promise.Response){
            val intent = Intent(activity, PendingActivity::class.java)
            intent.putExtra("promise",promise)
            activity.startActivity(intent)
        }

        fun openPromiseDetailScreen(appCompatActivity: AppCompatActivity, promise: PromiseResponse) {
            val intent = Intent(appCompatActivity, PromiseDetailActivity::class.java)
            intent.putExtra("promise", promise.promise)
            appCompatActivity.startActivity(intent)
        }

        fun openPromiseDetailPastScreen(appCompatActivity: AppCompatActivity, promise: PromiseResponse) {
            val intent = Intent(appCompatActivity, DetailPastActivity::class.java)
            intent.putExtra("promise", promise)
            appCompatActivity.startActivity(intent)

        }

        fun openLocationMap(appCompatActivity: AppCompatActivity, promise: Promise.Response){
            val intent = Intent(appCompatActivity, LocationMapActivity::class.java)
            intent.putExtra("promise",promise)
            appCompatActivity.startActivity(intent)
        }
    }
}

enum class PromiseState(val value : Int) {
    INVITING(0),
    PENDING(1),
    IMMINENT(2),
    CLOSE(3),
    DELETED(-1)
}

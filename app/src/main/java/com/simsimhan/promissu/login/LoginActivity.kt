package com.simsimhan.promissu.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import com.simsimhan.promissu.BuildConfig
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import com.simsimhan.promissu.databinding.ActivityLoginBinding
import com.simsimhan.promissu.util.NavigationUtil
import com.simsimhan.promissu.util.StringUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class LoginActivity : AppCompatActivity(), ISessionCallback {

    private val viewModel: LoginViewModel by viewModel()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.apply {
            lifecycleOwner = this@LoginActivity
            viewModel = this@LoginActivity.viewModel
        }

        val actionbar = supportActionBar
        actionbar?.hide()

        StringUtil.getHashKey(this)

        viewModel.toastMsg.observe(this, Observer {
            toastMessage(it)
        })

        viewModel.onSuccess.observe(this, Observer {
            if(it){
                NavigationUtil.replaceWithMainView(this)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        Session.getCurrentSession().addCallback(this)
    }

    override fun onStop() {
        super.onStop()
        Session.getCurrentSession().removeCallback(this)
    }


    override fun onSessionOpened() {
        UserManagement.getInstance().me(object : MeV2ResponseCallback() {
            override fun onSuccess(result: MeV2Response) {
                PromissuApplication.diskCache!!.setUserData(result.nickname, result.id, result.thumbnailImagePath)
                val userSessionToken = Session.getCurrentSession().tokenInfo.accessToken
                if (BuildConfig.DEBUG) {
                    toastMessage("[DEV] onSuccess() user token: $userSessionToken")
                }

                viewModel.login(userSessionToken,result)

            }

            override fun onSessionClosed(errorResult: ErrorResult) {
                if (BuildConfig.DEBUG) {
                    toastMessage("[DEV] onSessionClosed() check log")
                }
                Timber.e("onSessionClosed(): %s", errorResult.toString())
            }

        })
    }

    override fun onSessionOpenFailed(exception: KakaoException?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun toastMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}
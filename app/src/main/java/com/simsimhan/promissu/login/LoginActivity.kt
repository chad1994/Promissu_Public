package com.simsimhan.promissu.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.LoginButton
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import com.simsimhan.promissu.BuildConfig
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import com.simsimhan.promissu.databinding.ActivityLoginBinding
import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.Login
import com.simsimhan.promissu.util.NavigationUtil
import com.simsimhan.promissu.util.StringUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class LoginActivity_k : AppCompatActivity(), ISessionCallback {

    private val viewModel: LoginViewModel by viewModel()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.apply {
            lifecycleOwner = this@LoginActivity_k
            viewModel = this@LoginActivity_k.viewModel
        }

        val actionbar = supportActionBar
        actionbar?.hide()

        StringUtil.getHashKey(this)
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
        //        Session.getCurrentSession().checkAndImplicitOpen();
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
                    Toast.makeText(this@LoginActivity_k, "[DEV] onSuccess() user token: $userSessionToken", Toast.LENGTH_SHORT).show()
                }

                disposables.add(
                        PromissuApplication.retrofit!!
                                .create(AuthAPI::class.java)
                                .loginKakao(Login.Request(userSessionToken))
                                .doOnNext { next ->
                                    PromissuApplication.diskCache!!.setUserData(result.nickname, result.id, result.thumbnailImagePath)
                                    PromissuApplication.diskCache!!.setUserToken(next.token)
                                }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ onNext ->
                                    // save token
                                    NavigationUtil.replaceWithMainView(this@LoginActivity)
                                }, { onError ->
                                    Timber.e("onSessionClosed(): %s", onError.toString())
                                    Toast.makeText(this@LoginActivity_k, "서버 점검 중이거나, 인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
                                }))
            }

            override fun onSessionClosed(errorResult: ErrorResult) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(this@LoginActivity, "[DEV] onSessionClosed() check log", Toast.LENGTH_SHORT).show()
                }

                Timber.e("onSessionClosed(): %s", errorResult.toString())
            }


        })
    }

    override fun onSessionOpenFailed(exception: KakaoException?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun Toast

}
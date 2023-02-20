package com.pollvote.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.pollvote.R
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils
import kotlinx.coroutines.*

class SplashActivity : BaseActivity() {

    private val WAIT_TIME = 3 * 1000L
    private var isLogin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        CoroutineScope(Dispatchers.Default).launch {
            delay(WAIT_TIME)
            withContext(Dispatchers.Main) {
                afterSplash()
            }
        }
    }


    private fun afterSplash() {
        isLogin = SharedPrefrencesUtils.isUserLogin()!!

        if (!isLogin) {
            startActivity(
                Intent(this, SelectionActivity::class.java)
            )
            finish()
        } else {
            startActivity(
                Intent(this, EventListActivity::class.java)
            )
            finish()
        }

    }
}
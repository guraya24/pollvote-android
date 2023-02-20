package com.pollvote.application

import android.app.Application


import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        currentApplication = this;

        SharedPrefrencesUtils.init(applicationContext)
        SharedPrefrencesUtils.initRemember(applicationContext)
    }

    companion object {
        private var currentApplication: App? = null
        fun getInstance(): App? {
            return currentApplication
        }
    }


}
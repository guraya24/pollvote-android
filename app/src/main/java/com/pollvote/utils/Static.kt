package com.pollvote.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.text.TextUtils
import android.util.Patterns
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.pollvote.R
import com.pollvote.view.activities.SelectionActivity
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils


@Suppress("DEPRECATION")
class Static {


    fun statusBarColor(activity: Activity) {
        val window: Window = activity.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, R.color.app_base_color)
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target.toString()).matches()
    }

}
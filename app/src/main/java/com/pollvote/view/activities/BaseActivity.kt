package com.pollvote.view.activities

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.text.TextUtils
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pollvote.R
import com.pollvote.network.Constant
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils
import kotlinx.android.synthetic.main.activity_event_detail.*
import java.util.*


@Suppress("DEPRECATION")
open class BaseActivity : AppCompatActivity() {

    fun makeToast(msg: String) {
        /*Todo Logout user show dialog session expire */
        if (applicationContext != null) if (msg.toLowerCase(Locale.ROOT).contains("unauthorised")) {

            SharedPrefrencesUtils.clearUser()
            SharedPrefrencesUtils.setIsUserLogin(false)
            startActivity(Intent(this, SelectionActivity::class.java))
            finishAffinity()

        } else if (msg == Constant.NO_INTERNET.toString()) {
            noInternetDialog()
        } else {
            val toast: Toast = Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT)
//            val view = toast.view
//            val view1 = view?.findViewById<View>(android.R.id.message) as TextView
//            view1.setPadding(5, 5, 5, 5)
//            view1.setTextColor(Color.WHITE)
//            view1.textSize = 16f
//            view.setBackgroundResource(R.drawable.toast_bg)
            // Setting the Text Appearance
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                view1.setTextAppearance(R.style.toastTextStyle)
//            }
            toast.show()
        }
    }

//    open fun isValidPhone(phone: CharSequence?): Boolean {
//        return if (TextUtils.isEmpty(phone)) {
//            false
//        } else {
//            Patterns.PHONE.matcher(phone).matches()
//        }
//    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val v = currentFocus
        if (v != null &&
            (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) &&
            v is EditText &&
            !v.javaClass.name.startsWith("android.webkit.")
        ) {
            val scrcoords = IntArray(2)
            v.getLocationOnScreen(scrcoords)
            val x = ev.rawX + v.getLeft() - scrcoords[0]
            val y = ev.rawY + v.getTop() - scrcoords[1]
            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) hideKeyboard(
                this
            )
        }
        return super.dispatchTouchEvent(ev)
    }

    open fun hideKeyboard(activity: Activity?) {
        if (activity != null && activity.window != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
        }
    }

    private fun noInternetDialog() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_no_internet)


        val btn_retry = dialog.findViewById(R.id.btn_retry) as Button



        btn_retry.setOnClickListener {
            if (!isConnectedToInternet(this)) {
                btn_retry.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shaking))

            } else {

                dialog.dismiss()
                startActivity(intent);
                finish()
            }

        }

        dialog.show()
    }

    open fun isConnectedToInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivityManager.activeNetworkInfo
        if (info != null) {
            if (info.type == ConnectivityManager.TYPE_WIFI) {
                return true
            } else if (info.type == ConnectivityManager.TYPE_MOBILE) {
                return true
            }
        }
        return false
    }

    /*override fun onRestart() {

        // TODO Auto-generated method stub
        super.onRestart()
        recreate()
        finish()
        *//*  val i = Intent(this, activityName::class.java) //your class
          startActivity(i)
          finish()*//*
    }*/


}
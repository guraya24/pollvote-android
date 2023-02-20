package com.pollvote.view.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pollvote.R
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils
import com.pollvote.view.activities.SelectionActivity
import com.pollvote.view.interfaces.CallBackListener
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.dialog_event_filter.*
import kotlinx.android.synthetic.main.dialog_event_filter.img_close
import kotlinx.android.synthetic.main.dialog_logout.*

class LogoutDialog  :
    BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false

        //setStyle(DialogFragment.STYLE_NORMAL, R.style.fullscreen_dialog);

        return inflater.inflate(R.layout.dialog_logout, container, false)
    }

    override fun getTheme(): Int {
        return R.style.fullscreen_dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        img_close.setOnClickListener {
            dismiss()
        }

        btn_logout.setOnClickListener {
            val i = Intent(activity, SelectionActivity::class.java)
            activity?.startActivity(i)
            activity?.finishAffinity()
            SharedPrefrencesUtils.clearUser()
        }

        btn_cancel.setOnClickListener {
            dismiss()
        }
    }
}
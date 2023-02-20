package com.pollvote.view.dialogs

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pollvote.R
import com.pollvote.view.interfaces.CallBackListener
import com.pollvote.utils.sharedPreference.SharedPrefrencesUtils
import kotlinx.android.synthetic.main.dialog_event_filter.*

class EventFilterDialog(private val callBackListener: CallBackListener,val filter: String) :
    BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false

        //setStyle(DialogFragment.STYLE_NORMAL, R.style.fullscreen_dialog);

        return inflater.inflate(R.layout.dialog_event_filter, container, false)
    }

    override fun getTheme(): Int {
        return R.style.fullscreen_dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if(filter.equals("Vote")){
            btn_poll.setBackgroundResource(R.drawable.bg_button_gray)
            btn_vote.setBackgroundResource(R.drawable.bg_button)

            btn_poll.setTextColor(Color.BLACK)
            btn_vote.setTextColor(Color.WHITE)

        }

        if(filter.equals("Poll")){
            btn_poll.setBackgroundResource(R.drawable.bg_button)
            btn_vote.setBackgroundResource(R.drawable.bg_button_gray)


            btn_poll.setTextColor(Color.WHITE)
            btn_vote.setTextColor(Color.BLACK)
        }

        img_close.setOnClickListener {
            /* startActivity(
                 Intent(context, MainActivity::class.java)
             )
 */

            dismiss()
        }

        btn_poll.setOnClickListener {
            SharedPrefrencesUtils.isPoll = true
            SharedPrefrencesUtils.isVote = false
            callBackListener.callBack(SharedPrefrencesUtils.Vote, SharedPrefrencesUtils.isVote)
            callBackListener.callBack(SharedPrefrencesUtils.Poll, SharedPrefrencesUtils.isPoll)

            btn_poll.setBackgroundResource(R.drawable.bg_button)
            btn_vote.setBackgroundResource(R.drawable.bg_button_gray)


            btn_poll.setTextColor(Color.WHITE)
            btn_vote.setTextColor(Color.BLACK)

            dismiss()
        }

        btn_vote.setOnClickListener {
            SharedPrefrencesUtils.isPoll = false
            SharedPrefrencesUtils.isVote = true
            callBackListener.callBack(SharedPrefrencesUtils.Vote, SharedPrefrencesUtils.isVote)
            callBackListener.callBack(SharedPrefrencesUtils.Poll, SharedPrefrencesUtils.isPoll)

            btn_poll.setBackgroundResource(R.drawable.bg_button_gray)
            btn_vote.setBackgroundResource(R.drawable.bg_button)

            btn_poll.setTextColor(Color.BLACK)
            btn_vote.setTextColor(Color.WHITE)

            dismiss()
        }
    }
}



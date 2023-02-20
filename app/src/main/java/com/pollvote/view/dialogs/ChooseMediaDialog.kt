package com.pollvote.view.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pollvote.R
import com.pollvote.view.interfaces.BottomDialogListener
import kotlinx.android.synthetic.main.dialog_choose.*


class ChooseMediaDialog : BottomSheetDialogFragment() {

    var mDialogListener: BottomDialogListener? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)
//        (requireView().parent as view).setPadding(20, 0, 20, 15)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.dialog_choose, container, false)
//        if (arguments != null) {
//            data = requireArguments().getSerializable("data") as BankRespone.Data?
//        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        btn_camera?.setOnClickListener {
            dismiss()
            mDialogListener?.onCamera()
        }

        btn_gallery?.setOnClickListener {
            dismiss()
            mDialogListener?.onGallery()
        }
        img_close?.setOnClickListener {
            dismiss()
        }


        dialog!!.setOnKeyListener(DialogInterface.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                dismiss()
//                mOrderDeliveredDialogListener?.onSave("rating", "")
                return@OnKeyListener true
            }
            false
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mDialogListener = parent as BottomDialogListener
        } else {
            mDialogListener = context as BottomDialogListener
        }
    }

    override fun onDetach() {
        mDialogListener = null
        super.onDetach()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ChooseMediaDialog().apply {
                arguments = Bundle().apply {
                    isCancelable = false
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }


}
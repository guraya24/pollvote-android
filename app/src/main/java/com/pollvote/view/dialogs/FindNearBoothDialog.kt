package com.pollvote.view.dialogs

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pollvote.R
import com.pollvote.view.activities.ActivityFindNearByBooth
import kotlinx.android.synthetic.main.find_nearby_booth_dialog.*

class FindNearBoothDialog (val eventName:String,val address:String,val eventDate:String):
    BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false

        //setStyle(DialogFragment.STYLE_NORMAL, R.style.fullscreen_dialog);

        return inflater.inflate(R.layout.find_nearby_booth_dialog, container, false)
    }

    override fun getTheme(): Int {
        return R.style.fullscreen_dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        img_close.setOnClickListener {
            dismiss()
        }

        btn_OK.setOnClickListener {
            Dexter.withContext(activity)
                .withPermissions(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                    , Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object: MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if(report.areAllPermissionsGranted()){
                                startActivity(Intent(activity, ActivityFindNearByBooth::class.java)
                                    .putExtra("eventName",eventName)
                                    .putExtra("eventAddress",address)
                                    .putExtra("eventDate",eventDate))
                                dismiss()
                            }
                        }
                    }
                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        // Remember to invoke this method when the custom rationale is closed
                        // or just by default if you don't want to use any custom rationale.
                        token?.continuePermissionRequest()
                    }
                })
                .withErrorListener {

                }
                .check()

        }


    }
}
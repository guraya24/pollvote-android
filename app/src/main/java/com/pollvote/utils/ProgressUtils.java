package com.pollvote.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.pollvote.R;


public class ProgressUtils {

    private static Dialog progressDialog;

    public static void showLoadingDialog(Context context) {
        if (!(progressDialog != null && progressDialog.isShowing())) {
            progressDialog = new Dialog(context);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.dialog_progress);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    public static void cancelLoading() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
    }
}

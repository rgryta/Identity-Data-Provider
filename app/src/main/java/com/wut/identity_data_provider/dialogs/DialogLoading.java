package com.wut.identity_data_provider.dialogs;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.wut.identity_data_provider.R;

/**
 * Class used as a loading dialog so the application has some time to initialize.
 */
public class DialogLoading {

    private final Activity mActivity;
    private AlertDialog mDialog;

    /**
     * Dialog constructor.
     *
     * @param activity Provides activity class for the main activity on which the dialog is being displayed.
     */
    public DialogLoading(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * Method for starting the dialog.
     */
    @SuppressLint("InflateParams")
    public void startDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        LayoutInflater inflater = mActivity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_loading, null));
        builder.setCancelable(false);

        mDialog = builder.create();
        mDialog.show();
    }

    /**
     * Dialog dismissal method.
     */
    public void dismissDialog() {
        mDialog.dismiss();
    }
}

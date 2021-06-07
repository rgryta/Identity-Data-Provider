package com.wut.identity_data_provider.dialogs;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.wut.identity_data_provider.*;

public class DialogLoading {

    private final Activity mActivity;
    private AlertDialog mDialog;

    public DialogLoading(Activity activity){
        this.mActivity = activity;
    }

    @SuppressLint("InflateParams")
    public void startDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        LayoutInflater inflater = mActivity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_loading,null));
        builder.setCancelable(false);

        mDialog = builder.create();
        mDialog.show();
    }

    public void dismissDialog(){
        mDialog.dismiss();
    }
}

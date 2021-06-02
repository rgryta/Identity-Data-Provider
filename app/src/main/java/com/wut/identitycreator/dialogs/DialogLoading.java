package com.wut.identitycreator.dialogs;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.wut.identitycreator.*;

public class DialogLoading {

    private final Activity activity;
    private AlertDialog dialog;

    public DialogLoading(Activity activity){
        this.activity=activity;
    }

    @SuppressLint("InflateParams")
    public void startDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_loading,null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}

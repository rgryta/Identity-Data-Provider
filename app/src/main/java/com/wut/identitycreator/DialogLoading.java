package com.wut.identitycreator;


import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class DialogLoading {

    private Activity activity;
    private AlertDialog dialog;

    DialogLoading(Activity activity){
        this.activity=activity;
    }

    void startDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_loading,null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    void dismissDialog(){
        dialog.dismiss();
    }
}

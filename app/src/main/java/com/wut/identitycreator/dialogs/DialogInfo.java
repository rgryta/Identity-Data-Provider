package com.wut.identitycreator.dialogs;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wut.identitycreator.*;

import java.util.UUID;

public class DialogInfo {

    private final Activity activity;
    private AlertDialog dialog;

    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    public DialogInfo(Activity activity){
        this.activity=activity;
    }

    @SuppressLint("InflateParams")
    public void startDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_info,null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
        dialog.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismissDialog();
            }
            return true;
        });
        dialog.setCanceledOnTouchOutside(true);

        setText();

        dialog.findViewById(R.id.WelcomeTextHeader).setOnClickListener(view -> toggleWelcome(view));
        dialog.findViewById(R.id.ProjectTextHeader).setOnClickListener(view -> toggleWelcome(view));
        dialog.findViewById(R.id.InstructionsTextHeader).setOnClickListener(view -> toggleWelcome(view));
        dialog.findViewById(R.id.PrimaryTextHeader).setOnClickListener(view -> toggleWelcome(view));
        dialog.findViewById(R.id.OptionalTextHeader).setOnClickListener(view -> toggleWelcome(view));
        dialog.findViewById(R.id.AboutTextHeader).setOnClickListener(view -> toggleWelcome(view));
        dialog.findViewById(R.id.UUIDTextHeader).setOnClickListener(view -> toggleWelcome(view));

        dialog.findViewById(R.id.info_dialog_close).setOnClickListener(view -> dismissDialog());
    }

    public synchronized static String id(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.apply();
            }
        }
        return uniqueID;
    }

    public void dismissDialog(){
        dialog.dismiss();
    }

    private void setText(){
        Resources res = dialog.getContext().getResources();
        ((TextView)dialog.findViewById(R.id.UUIDTextContent)).setText(id(dialog.getContext()));
        ((TextView)dialog.findViewById(R.id.WelcomeTextHeader)).setText(res.getString(R.string.right_triangle)+res.getString(R.string.info_welcome_header));
        ((TextView)dialog.findViewById(R.id.ProjectTextHeader)).setText(res.getString(R.string.right_triangle) + res.getString(R.string.info_project_header));
        ((TextView)dialog.findViewById(R.id.InstructionsTextHeader)).setText(res.getString(R.string.right_triangle)+res.getString(R.string.info_instructions_header));
        ((TextView)dialog.findViewById(R.id.PrimaryTextHeader)).setText(res.getString(R.string.right_triangle)+res.getString(R.string.info_primary_header));
        ((TextView)dialog.findViewById(R.id.OptionalTextHeader)).setText(res.getString(R.string.right_triangle)+res.getString(R.string.info_optional_header));
        ((TextView)dialog.findViewById(R.id.AboutTextHeader)).setText(res.getString(R.string.right_triangle)+res.getString(R.string.info_about_header));
        ((TextView)dialog.findViewById(R.id.UUIDTextHeader)).setText(res.getString(R.string.right_triangle)+res.getString(R.string.info_uuid_header));
    }

    @SuppressLint("NonConstantResourceId")
    public void toggleWelcome(View v){
        TextView tv;
        String headerText;
        Resources res = dialog.getContext().getResources();
        switch (v.getId()){
            case R.id.WelcomeTextHeader:
                tv = dialog.findViewById(R.id.WelcomeTextContent);
                headerText = res.getString(R.string.info_welcome_header);
                break;
            case R.id.ProjectTextHeader:
                tv = dialog.findViewById(R.id.ProjectTextContent);
                headerText = res.getString(R.string.info_project_header);
                break;
            case R.id.InstructionsTextHeader:
                tv = dialog.findViewById(R.id.InstructionsTextContent);
                headerText = res.getString(R.string.info_instructions_header);
                break;
            case R.id.PrimaryTextHeader:
                tv = dialog.findViewById(R.id.PrimaryTextContent);
                headerText = res.getString(R.string.info_primary_header);
                break;
            case R.id.OptionalTextHeader:
                tv = dialog.findViewById(R.id.OptionalTextContent);
                headerText = res.getString(R.string.info_optional_header);
                break;
            case R.id.AboutTextHeader:
                tv = dialog.findViewById(R.id.AboutTextContent);
                headerText = res.getString(R.string.info_about_header);
                break;
            case R.id.UUIDTextHeader:
                tv = dialog.findViewById(R.id.UUIDTextContent);
                headerText = res.getString(R.string.info_uuid_header);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
        ViewGroup.LayoutParams params =  tv.getLayoutParams();
        switch (params.height){
            case ViewGroup.LayoutParams.WRAP_CONTENT:
                tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                ((TextView)v).setText(res.getString(R.string.right_triangle)+headerText);
                break;
            case 0:
                tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                ((TextView)v).setText(res.getString(R.string.down_triangle)+headerText);
                break;
            default:
                break;
        }


    }
}

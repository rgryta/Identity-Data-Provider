package com.wut.identity_data_provider.dialogs;


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
import android.widget.TextView;

import com.wut.identity_data_provider.*;

import java.util.UUID;

@SuppressWarnings("ALL")
public class DialogInfo {

    private final Activity mActivity;
    private AlertDialog mDialog;

    public DialogInfo(Activity activity){
        this.mActivity = activity;
    }

    @SuppressLint("InflateParams")
    public void startDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        LayoutInflater inflater = mActivity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_info,null));
        builder.setCancelable(false);

        mDialog = builder.create();
        mDialog.show();
        mDialog.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismissDialog();
            }
            return true;
        });
        mDialog.setCanceledOnTouchOutside(true);

        setText();

        mDialog.findViewById(R.id.WelcomeTextHeader).setOnClickListener(view -> toggleWelcome(view));
        mDialog.findViewById(R.id.ProjectTextHeader).setOnClickListener(view -> toggleWelcome(view));
        mDialog.findViewById(R.id.InstructionsTextHeader).setOnClickListener(view -> toggleWelcome(view));
        mDialog.findViewById(R.id.PrimaryTextHeader).setOnClickListener(view -> toggleWelcome(view));
        mDialog.findViewById(R.id.OptionalTextHeader).setOnClickListener(view -> toggleWelcome(view));
        mDialog.findViewById(R.id.AboutTextHeader).setOnClickListener(view -> toggleWelcome(view));
        mDialog.findViewById(R.id.UUIDTextHeader).setOnClickListener(view -> toggleWelcome(view));

        mDialog.findViewById(R.id.info_dialog_close).setOnClickListener(view -> dismissDialog());
    }

    public synchronized static String id(Context context) {
        String uniqueID;
        SharedPreferences sharedPrefs = context.getSharedPreferences(
                "PREF_UNIQUE_ID", Context.MODE_PRIVATE);
        uniqueID = sharedPrefs.getString("PREF_UNIQUE_ID", null);
        if (uniqueID == null) {
            uniqueID = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("PREF_UNIQUE_ID", uniqueID);
            editor.apply();
        }
        return uniqueID;
    }

    public void dismissDialog(){
        mDialog.dismiss();
    }

    @SuppressLint("SetTextI18n")
    private void setText(){
        Resources res = mDialog.getContext().getResources();
        ((TextView) mDialog.findViewById(R.id.UUIDTextContent)).setText(id(mDialog.getContext()));
        ((TextView) mDialog.findViewById(R.id.WelcomeTextHeader)).setText(res.getString(R.string.right_triangle)+res.getString(R.string.info_welcome_header));
        ((TextView) mDialog.findViewById(R.id.ProjectTextHeader)).setText(res.getString(R.string.right_triangle) + res.getString(R.string.info_project_header));
        ((TextView) mDialog.findViewById(R.id.InstructionsTextHeader)).setText(res.getString(R.string.right_triangle)+res.getString(R.string.info_instructions_header));
        ((TextView) mDialog.findViewById(R.id.PrimaryTextHeader)).setText(res.getString(R.string.right_triangle)+res.getString(R.string.info_primary_header));
        ((TextView) mDialog.findViewById(R.id.OptionalTextHeader)).setText(res.getString(R.string.right_triangle)+res.getString(R.string.info_optional_header));
        ((TextView) mDialog.findViewById(R.id.AboutTextHeader)).setText(res.getString(R.string.right_triangle)+res.getString(R.string.info_about_header));
        ((TextView) mDialog.findViewById(R.id.UUIDTextHeader)).setText(res.getString(R.string.right_triangle)+res.getString(R.string.info_uuid_header));
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    public void toggleWelcome(View v){
        View contentView;
        String headerText;
        Resources res = mDialog.getContext().getResources();
        switch (v.getId()){
            case R.id.WelcomeTextHeader:
                contentView = mDialog.findViewById(R.id.WelcomeTextContent);
                headerText = res.getString(R.string.info_welcome_header);
                break;
            case R.id.ProjectTextHeader:
                contentView = mDialog.findViewById(R.id.ProjectTextContent);
                headerText = res.getString(R.string.info_project_header);
                break;
            case R.id.InstructionsTextHeader:
                contentView = mDialog.findViewById(R.id.InstructionsTextContent);
                headerText = res.getString(R.string.info_instructions_header);
                break;
            case R.id.PrimaryTextHeader:
                contentView = mDialog.findViewById(R.id.PrimaryTextContent);
                headerText = res.getString(R.string.info_primary_header);
                break;
            case R.id.OptionalTextHeader:
                contentView = mDialog.findViewById(R.id.OptionalTextContent);
                headerText = res.getString(R.string.info_optional_header);
                break;
            case R.id.AboutTextHeader:
                contentView = mDialog.findViewById(R.id.AboutTextContent);
                headerText = res.getString(R.string.info_about_header);
                break;
            case R.id.UUIDTextHeader:
                contentView = mDialog.findViewById(R.id.UUIDTextContent);
                headerText = res.getString(R.string.info_uuid_header);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
        ViewGroup.LayoutParams params =  contentView.getLayoutParams();
        switch (params.height){
            case ViewGroup.LayoutParams.WRAP_CONTENT:
                contentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                ((TextView)v).setText(res.getString(R.string.right_triangle)+headerText);
                break;
            case 0:
                contentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                ((TextView)v).setText(res.getString(R.string.down_triangle)+headerText);
                break;
            default:
                break;
        }


    }
}

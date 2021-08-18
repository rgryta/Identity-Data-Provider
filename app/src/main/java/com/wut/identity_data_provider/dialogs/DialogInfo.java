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

import com.wut.identity_data_provider.R;

import java.util.UUID;

/**
 * Dialog class for displaying all the information about the application - usage, instructions etc.
 *
 */
public class DialogInfo {

    private final Activity mActivity;
    private AlertDialog mDialog;

    /**
     * Dialog constructor.
     *
     * @param activity   Provides activity class for the main activity on which the dialog is being displayed.
     */
    public DialogInfo(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * Method for starting the dialog, setting up the submenu options.
     *
     */
    @SuppressLint("InflateParams")
    public void startDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        LayoutInflater inflater = mActivity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_info, null));
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

        for (int i : new int[]{R.id.WelcomeTextHeader, R.id.ProjectTextHeader,
                R.id.InstructionsTextHeader, R.id.PrimaryTextHeader,
                R.id.OptionalTextHeader, R.id.AboutTextHeader, R.id.UUIDTextHeader})
            mDialog.findViewById(i).setOnClickListener(this::toggleWelcome);

        mDialog.findViewById(R.id.info_dialog_close).setOnClickListener(view -> dismissDialog());
    }

    /**
     * Method used to extract the UUID of the user/application instance.
     *
     * @param context   Context used to get the shared preferences.
     */
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

    /**
     * Dialog dismissal method.
     *
     */
    public void dismissDialog() {
        mDialog.dismiss();
    }

    /**
     * Method used to set initial text values to the info dialog submenus.
     *
     */
    @SuppressLint("SetTextI18n")
    private void setText() {
        Resources res = mDialog.getContext().getResources();
        ((TextView) mDialog.findViewById(R.id.UUIDTextContent)).setText(id(mDialog.getContext()));
        ((TextView) mDialog.findViewById(R.id.WelcomeTextHeader)).setText(res.getString(R.string.right_triangle) + res.getString(R.string.info_welcome_header));
        ((TextView) mDialog.findViewById(R.id.ProjectTextHeader)).setText(res.getString(R.string.right_triangle) + res.getString(R.string.info_project_header));
        ((TextView) mDialog.findViewById(R.id.InstructionsTextHeader)).setText(res.getString(R.string.right_triangle) + res.getString(R.string.info_instructions_header));
        ((TextView) mDialog.findViewById(R.id.PrimaryTextHeader)).setText(res.getString(R.string.right_triangle) + res.getString(R.string.info_primary_header));
        ((TextView) mDialog.findViewById(R.id.OptionalTextHeader)).setText(res.getString(R.string.right_triangle) + res.getString(R.string.info_optional_header));
        ((TextView) mDialog.findViewById(R.id.AboutTextHeader)).setText(res.getString(R.string.right_triangle) + res.getString(R.string.info_about_header));
        ((TextView) mDialog.findViewById(R.id.UUIDTextHeader)).setText(res.getString(R.string.right_triangle) + res.getString(R.string.info_uuid_header));
    }

    /**
     * Method used to toggle the submenus' header strings - arrows.
     *
     * @param v   View of the element that has to be toggled.
     */
    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    public void toggleWelcome(View v) {
        View contentView;
        String headerText;
        Resources res = mDialog.getContext().getResources();
        switch (v.getId()) {
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
        ViewGroup.LayoutParams params = contentView.getLayoutParams();
        switch (params.height) {
            case ViewGroup.LayoutParams.WRAP_CONTENT:
                contentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                ((TextView) v).setText(res.getString(R.string.right_triangle) + headerText);
                break;
            case 0:
                contentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                ((TextView) v).setText(res.getString(R.string.down_triangle) + headerText);
                break;
            default:
                break;
        }


    }
}

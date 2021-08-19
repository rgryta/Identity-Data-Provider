package com.wut.identity_data_provider.dialogs;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.wut.identity_data_provider.R;
import com.wut.identity_data_provider.activities.ActivityInput;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used for displaying a dialog of available users, and allowing to add new ones.
 */
public class DialogUsers {

    private final ActivityInput mActivity;
    private AlertDialog mDialog;

    /**
     * Dialog constructor.
     *
     * @param activity Provides activity class for the main activity on which the dialog is being displayed.
     */
    public DialogUsers(ActivityInput activity) {
        this.mActivity = activity;
    }

    /**
     * Method for starting the dialog amd setting its contents.
     *
     * @param users the users
     */
    @SuppressLint("InflateParams")
    public void startDialog(ArrayList<String> users) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        LayoutInflater inflater = mActivity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_users, null));
        builder.setCancelable(false);

        mDialog = builder.create();
        mDialog.show();

        final ListView listview = mDialog.findViewById(R.id.UserList);
        final ListArrayAdapter adapter = new ListArrayAdapter(mDialog.getContext(),
                R.layout.list_element_user, users);
        listview.setAdapter(adapter);
        mDialog.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismissDialog();
            }
            return true;
        });

        mDialog.setCanceledOnTouchOutside(true);

        final TextView acceptUser = mDialog.findViewById(R.id.new_user_accept);
        acceptUser.setOnClickListener(v -> {
            EditText editText = mDialog.findViewById(R.id.new_user_name);
            mActivity.addAndSetUser(editText.getText().toString());
            dismissDialog();
        });
    }

    /**
     * Dialog dismissal method.
     */
    public void dismissDialog() {
        mDialog.dismiss();
    }


    /**
     * List adapter class for organising the list of available users in a dialog.
     *
     */
    private class ListArrayAdapter extends ArrayAdapter<String> {

        /**
         * The M users.
         */
        final ArrayList<String> mUsers = new ArrayList<>();

        /**
         * User list class constructor.
         *
         * @param context            the context
         * @param textViewResourceId the text view resource id
         * @param objects            the objects
         */
        public ListArrayAdapter(Context context, int textViewResourceId,
                                List<String> objects) {
            super(context, textViewResourceId, objects);
            mUsers.addAll(objects);
        }


        /**
         * User list setup.
         *
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mDialog.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            assert inflater != null;
            @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.list_element_user, parent, false);
            TextView textView = rowView.findViewById(R.id.UserListElement);
            textView.setText(mUsers.get(position));
            textView.setOnClickListener(v -> {
                mActivity.addAndSetUser(mUsers.get(position));
                mDialog.dismiss();
            });

            return rowView;
        }

    }
}

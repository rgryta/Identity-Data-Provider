package com.wut.identitycreator.dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.wut.identitycreator.*;
import com.wut.identitycreator.activities.ActivityInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DialogUsers {

    private ActivityInput activity;
    private AlertDialog dialog;

    public DialogUsers(ActivityInput activity){
        this.activity=activity;
    }

    public void startDialog(ArrayList<String> users){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_users,null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();

        final ListView listview = dialog.findViewById(R.id.UserList);
        final ListArrayAdapter adapter = new ListArrayAdapter(dialog.getContext(),
                R.layout.list_element_user, users);
        listview.setAdapter(adapter);
        dialog.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismissDialog();
            }
            return true;
        });

        final TextView acceptUser = dialog.findViewById(R.id.new_user_accept);
        acceptUser.setOnClickListener(v -> {
            EditText editText = dialog.findViewById(R.id.new_user_name);
            activity.addAndSetUser(editText.getText().toString());
            dismissDialog();
        });
    }

    public void dismissDialog(){
        dialog.dismiss();
    }


    private class ListArrayAdapter extends ArrayAdapter<String> {

        ArrayList<String> users = new ArrayList<>();

        public ListArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            users.addAll(objects);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) dialog.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.list_element_user, parent, false);
            TextView textView = rowView.findViewById(R.id.UserListElement);
            textView.setText(users.get(position));
            textView.setOnClickListener(v -> {
                activity.addAndSetUser(users.get(position));
                dialog.dismiss();
            });

            return rowView;
        }

    }
}

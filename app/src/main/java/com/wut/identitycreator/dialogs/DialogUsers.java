package com.wut.identitycreator.dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wut.identitycreator.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DialogUsers {

    private Activity activity;
    private AlertDialog dialog;

    public DialogUsers(Activity activity){
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

            return rowView;
        }

    }
}

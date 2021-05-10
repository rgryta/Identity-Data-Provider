package com.wut.identitycreator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GridAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflter;
    int sWidth;

    RadioButton[] rbs = new RadioButton[9];

    ArrayList<Integer> passwd = new ArrayList<>();
    ArrayList<Integer> inPasswd = new ArrayList<>();

    public GridAdapter(Context applicationContext, int width) {
        context = applicationContext;
        sWidth = width;
        inflter = (LayoutInflater.from(applicationContext));

        passwd.add(0);
        passwd.add(2);
        passwd.add(7);
    }
    @Override
    public int getCount() {
        return 9;
    }

    @Override
    public Object getItem(int position){
        return rbs[position];
    };

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void checkItem(int position){
        RadioButton btn = (RadioButton)getItem(position);
         btn.setChecked(true);
    }

    public boolean clearItems(){

        boolean correct = true;
        if (inPasswd.size()==passwd.size()) {
            for (int i = 0; i<inPasswd.size(); i++) {
                if (inPasswd.get(i)!=passwd.get(i)){
                    correct=false;
                    break;
                }
            }
        }
        else correct=false;
        inPasswd.clear();
        for (int i = 0; i<getCount(); i++) {
            final RadioButton btn = (RadioButton) getItem(i);
            btn.setChecked(false);

            final int finalI = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inPasswd.add(finalI);
                    btn.setOnClickListener(null);
                }
            });
        }

        return correct;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(final int pointer, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.activity_grid, null); // inflate the layout

        ConstraintLayout vw = view.findViewById(R.id.gridID);
        vw.setMinHeight(sWidth);
        vw.setMaxHeight(sWidth);

        rbs[pointer] = view.findViewById(R.id.radioGrid);
        rbs[pointer].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inPasswd.add(pointer);
                rbs[pointer].setOnClickListener(null);
            }
        });


        //Position of radiobutton
        //int[] position = new int[2];
        //rbs[pointer].getLocationOnScreen(position);
        //int wd=rbs[pointer].getWidth()+position[0];
        //int hi=rbs[pointer].getHeight()+position[1];
        //("X: "+position[0] + " : " + wd);
        //("Y: "+position[1] + " : " + hi);

        return view;
    }

}
package com.wut.identitycreator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GridAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflter;
    int sWidth;

    RadioButton[] rbs = new RadioButton[9];
    PointF[] radioPoints = new PointF[9];

    ArrayList<Integer> passwd = new ArrayList<>();
    ArrayList<Integer> inPasswd = new ArrayList<>();

    int pointSize=0;

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

    public boolean verifyResult(){
        //check input passwd correctness
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

        return correct;
    }

    public List<PointF> getSelected(int statusHeight){
        if (pointSize==0) pointSize = Math.floorDiv(rbs[0].getWidth(),2);
        List<PointF> points = new ArrayList<>();
        for (int i : inPasswd){
            int[] pos = new int[2];
            rbs[i].getLocationOnScreen(pos);

            points.add(new PointF(pos[0]+pointSize,pos[1]+pointSize-statusHeight));
        }
        return points;
    }

    public void clearItems(){
        //reset radiobuttons
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

        return view;
    }

}
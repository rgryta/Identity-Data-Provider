package com.wut.identitycreator.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
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

import com.wut.identitycreator.*;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ViewGridAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    int sWidth;

    RadioButton[] rbs = new RadioButton[9];
    PointF[] radioPoints = new PointF[9];

    ArrayList<Integer> passwd = new ArrayList<>(); //correct
    ArrayList<Integer> inPasswd = new ArrayList<>(); //input

    int pointSize=0;

    public ViewGridAdapter(Context applicationContext, int width) {
        context = applicationContext;
        sWidth = width;
        inflater = (LayoutInflater.from(applicationContext));

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
        boolean result = inPasswd.equals(passwd);
        inPasswd.clear();
        return result;
    }

    public List<PointF> getSelected(float top){
        if (pointSize==0) pointSize = Math.floorDiv(rbs[0].getWidth(),2);
        List<PointF> points = new ArrayList<>();
        for (int i : inPasswd){
            Rect rect = new Rect();
            rbs[i].getGlobalVisibleRect(rect);

            points.add(new PointF(rect.centerX(),rect.centerY()-top));
        }
        return points;
    }

    public void clearItems(){
        for (int i = 0; i<getCount(); i++) {
            final RadioButton btn = (RadioButton) getItem(i);
            btn.setChecked(false);

            final int finalI = i;
            btn.setOnClickListener(v -> {
                inPasswd.add(finalI);
                btn.setOnClickListener(null);
            });

        }
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(final int pointer, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.view_grid, null); // inflate the layout

        ConstraintLayout vw = view.findViewById(R.id.gridID);
        vw.setMinHeight(sWidth);
        vw.setMaxHeight(sWidth);

        rbs[pointer] = view.findViewById(R.id.radioGrid);
        rbs[pointer].setOnClickListener(v -> {
            inPasswd.add(pointer);
            rbs[pointer].setOnClickListener(null);
        });

        return view;
    }

}
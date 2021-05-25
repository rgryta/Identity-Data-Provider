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
import android.widget.Toast;

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

    ArrayList<Integer> passwd = new ArrayList<>(); //correct
    ArrayList<Integer> inPasswd = new ArrayList<>(); //input

    public ViewGridAdapter(Context applicationContext, int width) {
        context = applicationContext;
        sWidth = width;
        inflater = (LayoutInflater.from(applicationContext));

        passwd.add(0);
        passwd.add(1);
        passwd.add(2);
        passwd.add(4);
        passwd.add(6);
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
                if (inPasswd.size()>0) {
                    intermediateToggles(finalI);
                }

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
            if (inPasswd.size()>0) {
                intermediateToggles(pointer);
            }

            inPasswd.add(pointer);
            rbs[pointer].setOnClickListener(null);
        });

        return view;
    }

    private void intermediateToggles(int pointer){
        switch (pointer) {
        case 0:
            if ((inPasswd.get(inPasswd.size() - 1) == 2) && (!inPasswd.contains(new Integer(1)))) {
                rbs[1].toggle();
                rbs[1].callOnClick();
            } else if ((inPasswd.get(inPasswd.size() - 1) == 6) && (!inPasswd.contains(new Integer(3)))) {
                rbs[3].toggle();
                rbs[3].callOnClick();
            } else if ((inPasswd.get(inPasswd.size() - 1) == 8) && (!inPasswd.contains(new Integer(4)))) {
                rbs[4].toggle();
                rbs[4].callOnClick();
            }
            break;
        case 1:
            System.out.println("TUT: Jestem");
            System.out.println("TUT: "+inPasswd.get(inPasswd.size() - 1));
            if ((inPasswd.get(inPasswd.size() - 1) == 7) && (!inPasswd.contains(new Integer(4)))) {
                rbs[4].toggle();
                rbs[4].callOnClick();
            }
            break;
        case 2:
            if ((inPasswd.get(inPasswd.size() - 1) == 0) && (!inPasswd.contains(new Integer(1)))) {
                rbs[1].toggle();
                rbs[1].callOnClick();
            } else if ((inPasswd.get(inPasswd.size() - 1) == 8) && (!inPasswd.contains(new Integer(5)))) {
                rbs[5].toggle();
                rbs[5].callOnClick();
            } else if ((inPasswd.get(inPasswd.size() - 1) == 7) && (!inPasswd.contains(new Integer(4)))) {
                rbs[4].toggle();
                rbs[4].callOnClick();
            }
            break;
        case 3:
            if ((inPasswd.get(inPasswd.size() - 1) == 5) && (!inPasswd.contains(new Integer(4)))) {
                rbs[4].toggle();
                rbs[4].callOnClick();
            }
            break;
        case 5:
            if ((inPasswd.get(inPasswd.size() - 1) == 3) && (!inPasswd.contains(new Integer(4)))) {
                rbs[4].toggle();
                rbs[4].callOnClick();
            }
            break;
        case 6:
            if ((inPasswd.get(inPasswd.size() - 1) == 8) && (!inPasswd.contains(new Integer(7)))) {
                rbs[7].toggle();
                rbs[7].callOnClick();
            } else if ((inPasswd.get(inPasswd.size() - 1) == 0) && (!inPasswd.contains(new Integer(3)))) {
                rbs[3].toggle();
                rbs[3].callOnClick();
            } else if ((inPasswd.get(inPasswd.size() - 1) == 2) && (!inPasswd.contains(new Integer(4)))) {
                rbs[4].toggle();
                rbs[4].callOnClick();
            }
            break;
        case 7:
            if ((inPasswd.get(inPasswd.size() - 1) == 1) && (!inPasswd.contains(new Integer(4)))) {
                rbs[4].toggle();
                rbs[4].callOnClick();
            }
            break;
        case 8:
            if ((inPasswd.get(inPasswd.size() - 1) == 6) && (!inPasswd.contains(new Integer(7)))) {
                rbs[7].toggle();
                rbs[7].callOnClick();
            } else if ((inPasswd.get(inPasswd.size() - 1) == 2) && (!inPasswd.contains(new Integer(5)))) {
                rbs[5].toggle();
                rbs[5].callOnClick();
            } else if ((inPasswd.get(inPasswd.size() - 1) == 0) && (!inPasswd.contains(new Integer(4)))) {
                rbs[4].toggle();
                rbs[4].callOnClick();
            }
            break;
        default:
    }

    }
}

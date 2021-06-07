package com.wut.identitycreator.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

import com.wut.identitycreator.*;

import org.json.JSONException;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ViewGridAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    int sWidth;

    RadioButton[] rbs = new RadioButton[9];
    TextView[] textId = new TextView[9];

    ArrayList<Integer> passwd = new ArrayList<>(); //correct
    ArrayList<Integer> inPasswd = new ArrayList<>(); //input



    public ViewGridAdapter(Context applicationContext, int width, String pattern) {
        context = applicationContext;
        sWidth = width;
        inflater = (LayoutInflater.from(applicationContext));

        passwd.addAll(parsePattern(pattern));
    }
    @Override
    public int getCount() {
        return 9;
    }

    @Override
    public Object getItem(int position){
        return rbs[position];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public ArrayList<Integer> parsePattern(String pattern){
        ArrayList<Integer> password = new ArrayList<>();
        String[] splitPattern = pattern.split("-");
        if (splitPattern.length>1) for (String s : splitPattern) password.add(Integer.valueOf(s));
        return password;
    }

    public JSONObject calibrationSetting() throws JSONException{
        JSONObject calibration = new JSONObject();
        for (int i=0; i<rbs.length; i++){
            Rect rect = new Rect();
            rbs[i].getGlobalVisibleRect(rect);
            JSONObject button = new JSONObject();
                button.put("left",rect.left);
                button.put("right",rect.right);
                button.put("top",rect.top);
                button.put("bottom",rect.bottom);
                calibration.put(String.valueOf(i),button);
        }
        return calibration;
    }


    public boolean verifyResult(){
        //check input passwd correctness
        if (passwd.size()==0){
            if (inPasswd.size()<4) {
                getAndClearInPasswd();
                return false;
            }
        }
        else {
            boolean result = inPasswd.equals(passwd);
            getAndClearInPasswd();
            return result;
        }
        return true;
    }

    public ArrayList<Integer> getAndClearInPasswd(){
        ArrayList<Integer> passwd = new ArrayList<>(inPasswd);
        inPasswd.clear();
        return passwd;
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

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(final int pointer, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.view_grid, null); // inflate the layout

        ConstraintLayout vw = view.findViewById(R.id.gridID);
        vw.setMinHeight(sWidth);
        vw.setMaxHeight(sWidth);

        textId[pointer] = view.findViewById(R.id.radioId);
        int idx = passwd.indexOf(pointer);
        if (idx==-1) textId[pointer].setText("");
        else textId[pointer].setText(String.valueOf(idx+1));

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
            if ((inPasswd.get(inPasswd.size() - 1) == 2) && (!inPasswd.contains(1))) {
                rbs[1].toggle();
                rbs[1].callOnClick();
            } else if ((inPasswd.get(inPasswd.size() - 1) == 6) && (!inPasswd.contains(3))) {
                rbs[3].toggle();
                rbs[3].callOnClick();
            } else if ((inPasswd.get(inPasswd.size() - 1) == 8) && (!inPasswd.contains(4))) {
                rbs[4].toggle();
                rbs[4].callOnClick();
            }
            break;
        case 1:
            if ((inPasswd.get(inPasswd.size() - 1) == 7) && (!inPasswd.contains(4))) {
                rbs[4].toggle();
                rbs[4].callOnClick();
            }
            break;
        case 2:
            if ((inPasswd.get(inPasswd.size() - 1) == 0) && (!inPasswd.contains(1))) {
                rbs[1].toggle();
                rbs[1].callOnClick();
            } else if ((inPasswd.get(inPasswd.size() - 1) == 8) && (!inPasswd.contains(5))) {
                rbs[5].toggle();
                rbs[5].callOnClick();
            } else if ((inPasswd.get(inPasswd.size() - 1) == 7) && (!inPasswd.contains(4))) {
                rbs[4].toggle();
                rbs[4].callOnClick();
            }
            break;
        case 3:
            if ((inPasswd.get(inPasswd.size() - 1) == 5) && (!inPasswd.contains(4))) {
                rbs[4].toggle();
                rbs[4].callOnClick();
            }
            break;
        case 5:
            if ((inPasswd.get(inPasswd.size() - 1) == 3) && (!inPasswd.contains(4))) {
                rbs[4].toggle();
                rbs[4].callOnClick();
            }
            break;
        case 6:
            if ((inPasswd.get(inPasswd.size() - 1) == 8) && (!inPasswd.contains(7))) {
                rbs[7].toggle();
                rbs[7].callOnClick();
            } else if ((inPasswd.get(inPasswd.size() - 1) == 0) && (!inPasswd.contains(3))) {
                rbs[3].toggle();
                rbs[3].callOnClick();
            } else if ((inPasswd.get(inPasswd.size() - 1) == 2) && (!inPasswd.contains(4))) {
                rbs[4].toggle();
                rbs[4].callOnClick();
            }
            break;
        case 7:
            if ((inPasswd.get(inPasswd.size() - 1) == 1) && (!inPasswd.contains(4))) {
                rbs[4].toggle();
                rbs[4].callOnClick();
            }
            break;
        case 8:
            if ((inPasswd.get(inPasswd.size() - 1) == 6) && (!inPasswd.contains(7))) {
                rbs[7].toggle();
                rbs[7].callOnClick();
            } else if ((inPasswd.get(inPasswd.size() - 1) == 2) && (!inPasswd.contains(5))) {
                rbs[5].toggle();
                rbs[5].callOnClick();
            } else if ((inPasswd.get(inPasswd.size() - 1) == 0) && (!inPasswd.contains(4))) {
                rbs[4].toggle();
                rbs[4].callOnClick();
            }
            break;
        default:
    }
    }
}

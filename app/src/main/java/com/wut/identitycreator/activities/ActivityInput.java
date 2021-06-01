package com.wut.identitycreator.activities;

import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import com.wut.identitycreator.data.DataDBHandler;
import com.wut.identitycreator.data.DataDBHelper;
import com.wut.identitycreator.data.DataDBSchema;
import com.wut.identitycreator.dialogs.DialogLoading;
import com.wut.identitycreator.dialogs.DialogUsers;
import com.wut.identitycreator.views.ViewGridAdapter;
import com.wut.identitycreator.views.ViewDrawPath;

import com.wut.identitycreator.*;


@RequiresApi(api = Build.VERSION_CODES.O)
public class ActivityInput extends Activity {

    GridView radioGrid;

    ViewDrawPath mViewDrawPath;

    DataDBHandler dbHandler;

    //mode = input OR calib
    String mode;

    private ViewGridAdapter adapter;
    private int sSide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        //Block screen with loading dialog
        DialogLoading dialog = new DialogLoading(this);
        dialog.startDialog();


        try {
            dbHandler = new DataDBHandler(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        setHeader();

        // Input area calibration
        View view = findViewById(R.id.submain_activity);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        int sOff = Math.floorDiv(width,10); //square offset
        sSide = width-2*sOff;

        if (dbHandler.settings.get("CALIB").equals("-1")) {
            mode="CALIB";

            int height = size.y - width;
            int hOff = Math.floorDiv(height,2);
            view.setPadding(sOff,(int)Math.round((hOff+sOff)/10.0)*10,sOff,0);

            View v = findViewById(R.id.INPUT_MODE);
            v.setLayoutParams(new RelativeLayout.LayoutParams(0, RelativeLayout.LayoutParams.MATCH_PARENT));

            v = findViewById(R.id.CALIB_MODE);
            v.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        }
        else {
            mode="INPUT";
            view.setPadding(sOff, Integer.parseInt(dbHandler.settings.get("CALIB")),sOff,0);
        }

        // Create an object of CustomAdapter and set Adapter to GirdView
        radioGrid = findViewById(R.id.radioGrid); // init GridView
        adapter = new ViewGridAdapter(getApplicationContext(),Math.floorDiv(sSide,3),dbHandler.settings.get("PATTERN"));

        radioGrid.setAdapter(adapter);

        mViewDrawPath = (ViewDrawPath)findViewById(R.id.passPath);

        //unblock screen after 2 seconds (initialization - getting x/y values for the grid points
        //there doesn't seem to eb
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> dialog.dismissDialog(), 2000);
    }


    @Override
    public boolean dispatchTouchEvent (MotionEvent ev){

        System.out.println("----------");
        //System.out.println(ev.getX());
        //System.out.println(ev.getY());

        //Not usable
        /*
        System.out.println(ev.getSize());
        System.out.println(ev.getPressure());

        System.out.println(ev.getTouchMajor());
        System.out.println(ev.getTouchMinor());
        System.out.println(ev.getToolMajor());
        System.out.println(ev.getToolMinor());
        */

        View v = findViewById(R.id.passPath);
        int topHeight = v.getTop()+getStatusHeight();
        if ((ev.getY()>topHeight) && (ev.getY()<(topHeight+v.getHeight()))){
            if (mode.equals("INPUT")){
                return handleInput(ev,topHeight);
            }
            else if (mode.equals("CALIB")){
                return handleCalib(ev);
            }
        }
        else {
            resetPass();
            return super.dispatchTouchEvent(ev);
        }
        return true;
    }

    private boolean handleInput(MotionEvent ev, float top){
        if ((ev.getAction()==MotionEvent.ACTION_DOWN)||(ev.getAction()==MotionEvent.ACTION_MOVE)){
            ev.setAction(MotionEvent.ACTION_DOWN);
            super.dispatchTouchEvent(ev);
            ev.setAction(MotionEvent.ACTION_UP);
            boolean res = super.dispatchTouchEvent(ev);

            List<PointF> points = adapter.getSelected(top);
            points.add(new PointF(ev.getX(),ev.getY()-top));

            mViewDrawPath.resetPoints(points);
            mViewDrawPath.draw();

            return res;
        }
        // When lifting finger - check passcode for validity and reset the buttons
        else if (ev.getAction()==MotionEvent.ACTION_UP){
            boolean res = adapter.verifyResult();
            System.out.println("Rezultat: "+res);

            resetPass();
        }
        return true;
    }

    private boolean handleCalib(MotionEvent ev){
        View v = findViewById(R.id.submain_activity);
        View v2 = findViewById(R.id.radioGrid);
        Rect rect = new Rect();
        v2.getDrawingRect(rect);

        int topPadding = (int)ev.getY()-(v.getTop()+getStatusHeight()+rect.width()/2);

        System.out.println(topPadding);
        System.out.println(v.getHeight());
        System.out.println(rect.width()/2);
        if (topPadding>v.getHeight()-rect.width()){
            topPadding = v.getHeight()-rect.width();
        }
        else if (topPadding<0) topPadding=0;

        v.setPadding(v.getPaddingLeft(),(int)Math.round(topPadding/50.0)*50,v.getPaddingRight(),0);
        return true;
    }

    private void resetPass(){
        mViewDrawPath.clearPoints();
        mViewDrawPath.draw();
        adapter = new ViewGridAdapter(getApplicationContext(),Math.floorDiv(sSide,3),dbHandler.settings.get("PATTERN"));
        radioGrid.setAdapter(adapter);
    }

    private int getStatusHeight(){
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void switchMode(){
        if (mode.equals("CALIB")){
            mode="INPUT";
            View v = findViewById(R.id.INPUT_MODE);
            v.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            v = findViewById(R.id.CALIB_MODE);
            v.setLayoutParams(new RelativeLayout.LayoutParams(0, RelativeLayout.LayoutParams.MATCH_PARENT));
        }
        else {
            mode="CALIB";
            View v = findViewById(R.id.INPUT_MODE);
            v.setLayoutParams(new RelativeLayout.LayoutParams(0, RelativeLayout.LayoutParams.MATCH_PARENT));

            v = findViewById(R.id.CALIB_MODE);
            v.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        }
    }


    public void switchMode(View view) {
        switchMode();
    }


    public void backFromCalib(View view){
        if (dbHandler.settings.get("CALIB").equals("-1")){
            Toast.makeText(getApplicationContext(),R.string.firstCalib,Toast.LENGTH_SHORT).show();
        }
        else {

            View v = findViewById(R.id.submain_activity);
            v.setPadding(v.getPaddingLeft(), Integer.parseInt(dbHandler.settings.get("CALIB")),v.getPaddingRight(),0);

            resetPass();

            switchMode();
        }
    }

    public void saveCalib(View view){
            View v = findViewById(R.id.submain_activity);
            dbHandler.addAndSetCalib(String.valueOf(v.getPaddingTop()));

            v.setPadding(v.getPaddingLeft(), Integer.parseInt(dbHandler.settings.get("CALIB")),v.getPaddingRight(),0);

            resetPass();

            switchMode();
    }


    public void setHeader(){
        TextView v = findViewById(R.id.User);
        v.setText(dbHandler.settings.get("USER"));
        v = findViewById(R.id.Pattern);
        v.setText(dbHandler.settings.get("PATTERN"));
    }

    public void patternLeft(View view) {
        int idx = dbHandler.patterns.indexOf(dbHandler.settings.get("PATTERN"));
        if (idx>0) idx--;
        else idx = dbHandler.patterns.size()-1;
        dbHandler.setConfigPattern(idx);
        setHeader();
        resetPass();
    }

    public void patternRight(View view) {
        int idx = dbHandler.patterns.indexOf(dbHandler.settings.get("PATTERN"));
        if (idx>=dbHandler.patterns.size()-1) idx=0;
        else idx++;
        dbHandler.setConfigPattern(idx);
        setHeader();
        resetPass();
    }

    public void startUserDialog(View view) {
        DialogUsers dialog = new DialogUsers(this);
        dialog.startDialog(dbHandler.users);
    }
}


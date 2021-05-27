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
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import com.wut.identitycreator.data.DataDBHandler;
import com.wut.identitycreator.data.DataDBHelper;
import com.wut.identitycreator.data.DataDBSchema;
import com.wut.identitycreator.dialogs.DialogLoading;
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

            int height = size.y;
            height -= width;
            int hOff = Math.floorDiv(height,2);
            view.setPadding(sOff,hOff+sOff,sOff,0);
        }
        else {
            mode="INPUT";
            view.setPadding(sOff, Integer.parseInt(dbHandler.settings.get("CALIB")),sOff,0);
        }

        // Create an object of CustomAdapter and set Adapter to GirdView
        radioGrid = findViewById(R.id.radioGrid); // init GridView
        adapter = new ViewGridAdapter(getApplicationContext(),Math.floorDiv(sSide,3));

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


        if (mode.equals("INPUT")){
            View v = findViewById(R.id.passPath);

            Rect rect = new Rect();
            v.getDrawingRect(rect);

            return handleInput(ev,v.getTop()+getStatusHeight());
        }
        else if (mode.equals("CALIB")){

            View v = findViewById(R.id.submain_activity);

            //if ((ev.getY()>(v.getTop()+getStatusHeight())) && (ev.getY()<(v.getTop()+getStatusHeight()+v.getHeight()))) System.out.println("JESTEM");
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

            v.setPadding(v.getPaddingLeft(),topPadding,v.getPaddingRight(),0);

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
            mViewDrawPath.clearPoints();
            mViewDrawPath.draw();

            boolean res = adapter.verifyResult();
            System.out.println("Rezultat: "+res);

            adapter = new ViewGridAdapter(getApplicationContext(),Math.floorDiv(sSide,3));
            radioGrid.setAdapter(adapter);
        }
        return true;
    }

    private int getStatusHeight(){
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}


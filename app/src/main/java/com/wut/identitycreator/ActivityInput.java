package com.wut.identitycreator;

import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

import java.util.List;


@RequiresApi(api = Build.VERSION_CODES.O)
public class ActivityInput extends Activity {

    GridView radioGrid;

    PathView mPathView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Block screen with loading dialog
        DialogLoading dialog = new DialogLoading(this);
        dialog.startDialog();

        setContentView(R.layout.view_grid);
        View view = findViewById(R.id.submain_activity);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        height -= width;
        int hOff = Math.floorDiv(height,2);
        int sOff = Math.floorDiv(width,10); //square offset
        int sSide = width-2*sOff;

        view.setPadding(sOff,hOff+sOff,sOff,0);

        // Create an object of CustomAdapter and set Adapter to GirdView
        radioGrid = findViewById(R.id.radioGrid); // init GridView
        GridAdapter customAdapter = new GridAdapter(getApplicationContext(),Math.floorDiv(sSide,3));

        radioGrid.setAdapter(customAdapter);

        mPathView = (PathView)findViewById(R.id.passPath);

        //unblock screen after 2 seconds (initialization - getting x/y values for the grid points
        //there doesn't seem to eb
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismissDialog();
            }
        }, 2000);

    }


    @Override
    public boolean dispatchTouchEvent (MotionEvent ev){

        System.out.println("----------");
        System.out.println(ev.getX());
        System.out.println(ev.getY());

        //Not usable
        /*
        System.out.println(ev.getSize());
        System.out.println(ev.getPressure());

        System.out.println(ev.getTouchMajor());
        System.out.println(ev.getTouchMinor());
        System.out.println(ev.getToolMajor());
        System.out.println(ev.getToolMinor());
        */

        final GridAdapter adapter = (GridAdapter)radioGrid.getAdapter();

        //for each action execute press action
        if ((ev.getAction()==MotionEvent.ACTION_DOWN)||(ev.getAction()==MotionEvent.ACTION_MOVE)){

            int statusHeight = getStatusHeight();
            List<PointF> points = adapter.getSelected(statusHeight);
            points.add(new PointF(ev.getX(),ev.getY()-statusHeight));

            mPathView.resetPoints(points);
            mPathView.draw();

            ev.setAction(MotionEvent.ACTION_DOWN);
            super.dispatchTouchEvent(ev);
            ev.setAction(MotionEvent.ACTION_UP);
            return super.dispatchTouchEvent(ev);
        }
        //when lifting finger - check passcode for validity and reset the buttons
        else if (ev.getAction()==MotionEvent.ACTION_UP){
            mPathView.clearPoints();
            mPathView.draw();

            boolean res = adapter.verifyResult();
            System.out.println("Resultat: "+res);

            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.clearItems();
                }
            }, 5);


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


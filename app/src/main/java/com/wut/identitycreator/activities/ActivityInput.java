package com.wut.identitycreator.activities;

import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.database.Cursor;
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
import android.widget.Toast;

import java.io.IOException;
import java.util.Currency;
import java.util.List;

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

    DataDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Block screen with loading dialog
        DialogLoading dialog = new DialogLoading(this);
        dialog.startDialog();

        try {
            dbHelper = new DataDBHelper(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }


        Cursor cursor = dbHelper.db.query(DataDBSchema.User.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        String name = null;
        while (cursor.moveToNext()){
            name = cursor.getString(0);
        }

        Toast.makeText(getBaseContext(),name,
                Toast.LENGTH_SHORT).show();

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
        ViewGridAdapter customAdapter = new ViewGridAdapter(getApplicationContext(),Math.floorDiv(sSide,3));

        radioGrid.setAdapter(customAdapter);

        mViewDrawPath = (ViewDrawPath)findViewById(R.id.passPath);

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

        final ViewGridAdapter adapter = (ViewGridAdapter)radioGrid.getAdapter();

        //for each action execute press action
        if ((ev.getAction()==MotionEvent.ACTION_DOWN)||(ev.getAction()==MotionEvent.ACTION_MOVE)){

            int statusHeight = getStatusHeight();
            List<PointF> points = adapter.getSelected(statusHeight);
            points.add(new PointF(ev.getX(),ev.getY()-statusHeight));

            mViewDrawPath.resetPoints(points);
            mViewDrawPath.draw();

            ev.setAction(MotionEvent.ACTION_DOWN);
            super.dispatchTouchEvent(ev);
            ev.setAction(MotionEvent.ACTION_UP);
            return super.dispatchTouchEvent(ev);
        }
        //when lifting finger - check passcode for validity and reset the buttons
        else if (ev.getAction()==MotionEvent.ACTION_UP){
            mViewDrawPath.clearPoints();
            mViewDrawPath.draw();

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


package com.wut.identitycreator.activities;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wut.identitycreator.R;
import com.wut.identitycreator.data.DataDBHandler;
import com.wut.identitycreator.dialogs.DialogInfo;
import com.wut.identitycreator.dialogs.DialogLoading;
import com.wut.identitycreator.dialogs.DialogUsers;
import com.wut.identitycreator.views.ViewDrawPath;
import com.wut.identitycreator.views.ViewGridAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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


        dbHandler = new DataDBHandler(this);

        setHeader();

        // Input area calibration
        View view = findViewById(R.id.submain_activity);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        int sOff = Math.floorDiv(width,10); //square offset
        sSide = width-2*sOff;

        if (Objects.equals(dbHandler.settings.get("CALIB"), "-1")) {
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
            view.setPadding(sOff, Integer.parseInt(Objects.requireNonNull(dbHandler.settings.get("CALIB"))),sOff,0);
        }

        setHeaderMode();

        // Create an object of CustomAdapter and set Adapter to GirdView
        radioGrid = findViewById(R.id.radioGrid); // init GridView
        adapter = new ViewGridAdapter(this,Math.floorDiv(sSide,3),dbHandler.settings.get("PATTERN"));

        radioGrid.setAdapter(adapter);

        mViewDrawPath = findViewById(R.id.passPath);

        //unblock screen after 2 seconds (initialization - getting x/y values for the grid points
        //there doesn't seem to eb
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            dialog.dismissDialog();
            if (Objects.equals(dbHandler.settings.get("CALIB"), "-1")) {
                startInfoDialog(findViewById(R.id.PW));
            }
        }, 2000);
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
            if (res) {
                if (Objects.equals(dbHandler.settings.get("PATTERN"), "")){
                    ArrayList<Integer> newPattern = new ArrayList<>(adapter.getAndClearInPasswd());
                    dbHandler.addAndSetNewPattern(newPattern);

                    dbHandler = new DataDBHandler(this);
                }
                else {
                    parseAndAddEntry(ev);
                }
                setHeader();
            }
            else {
                if (Objects.equals(dbHandler.settings.get("PATTERN"), "")){
                    Toast.makeText(this, R.string.error_new_pattern_length,Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, R.string.error_wrong_pattern, Toast.LENGTH_SHORT).show();
                }
            }

            resetPass();
        }
        return true;
    }

    private int startY;
    private int startPadding;

    private boolean handleCalib(MotionEvent ev){
        View v = findViewById(R.id.submain_activity);
        if (ev.getAction()==MotionEvent.ACTION_DOWN) {
            startY=(int)ev.getY();
            startPadding = v.getPaddingTop();
        }
        else{
            View v2 = findViewById(R.id.radioGrid);
            Rect rect = new Rect();
            v2.getDrawingRect(rect);

            int topPadding = startPadding+(int)ev.getY()-startY;

            if (topPadding>v.getHeight()-rect.width()){
                topPadding = v.getHeight()-rect.width();
            }
            else if (topPadding<0) topPadding=0;

            topPadding = (int)Math.round(topPadding/50.0)*50;

            v.setPadding(v.getPaddingLeft(),topPadding,v.getPaddingRight(),0);

            TextView calibTests = findViewById(R.id.calib_data_entries_num);
            int count = dbHandler.completedTestsForCalib(String.valueOf(topPadding));
            calibTests.setText(getResources().getQuantityString(R.plurals.completed_tests,count,count));

        }
        return true;
    }

    private void resetPass(){
        mViewDrawPath.clearPoints();
        mViewDrawPath.draw();
        adapter = new ViewGridAdapter(this,Math.floorDiv(sSide,3),dbHandler.settings.get("PATTERN"));
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
        }
        else {
            mode="CALIB";
        }
        setHeaderMode();
    }

    private void setHeaderMode(){
        View v;
        switch (mode){
            case "CALIB":
                v = findViewById(R.id.INPUT_MODE);
                v.setLayoutParams(new RelativeLayout.LayoutParams(0, RelativeLayout.LayoutParams.MATCH_PARENT));

                v = findViewById(R.id.CALIB_MODE);
                v.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                break;
            case "INPUT":
                v = findViewById(R.id.INPUT_MODE);
                v.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT));

                v = findViewById(R.id.CALIB_MODE);
                v.setLayoutParams(new RelativeLayout.LayoutParams(0, RelativeLayout.LayoutParams.MATCH_PARENT));
                break;
            default:
                break;
        }
    }


    public void switchMode(View view) {
        switchMode();
    }


    public void backFromCalib(View view){
        if (Objects.equals(dbHandler.settings.get("CALIB"), "-1")){
            Toast.makeText(this,R.string.error_msg_first_calibration,Toast.LENGTH_SHORT).show();
        }
        else {

            View v = findViewById(R.id.submain_activity);
            v.setPadding(v.getPaddingLeft(), Integer.parseInt(Objects.requireNonNull(dbHandler.settings.get("CALIB"))),v.getPaddingRight(),0);

            resetPass();

            switchMode();
        }
    }

    public void saveCalib(View view){
            View v = findViewById(R.id.submain_activity);
            dbHandler.addAndSetCalib(String.valueOf(v.getPaddingTop()));

            v.setPadding(v.getPaddingLeft(), Integer.parseInt(Objects.requireNonNull(dbHandler.settings.get("CALIB"))),v.getPaddingRight(),0);

            resetPass();
            switchMode();
            setHeader();
    }


    public void setHeader(){
        TextView v = findViewById(R.id.User);
        v.setText(dbHandler.settings.get("USER"));
        v = findViewById(R.id.Pattern);
        String patternHeader = "";
        if (!Objects.equals(dbHandler.settings.get("PATTERN"), "")){
            patternHeader = dbHandler.settings.get("PATTERN")+" ("+dbHandler.completedTests()+")";
        }
        v.setText(patternHeader);
        v = findViewById(R.id.calib_data_entries_num);
        int count = dbHandler.completedTestsForCalib(dbHandler.settings.get("CALIB"));
        v.setText(getResources().getQuantityString(R.plurals.completed_tests,count,count));
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
        int hasGoodPattern = dbHandler.checkProgressAndSetBestCalib(true);
        if (hasGoodPattern!=-1) {
            View v = findViewById(R.id.submain_activity);
            v.setPadding(v.getPaddingLeft(), Integer.parseInt(Objects.requireNonNull(dbHandler.settings.get("CALIB"))),v.getPaddingRight(),0);
            DialogUsers dialog = new DialogUsers(this);
            dialog.startDialog(dbHandler.users);
        }
        else {
            Toast.makeText(this, R.string.error_new_user_primary_tests,Toast.LENGTH_LONG).show();
        }
    }

    public void startInfoDialog(View view) {
        DialogInfo dialog = new DialogInfo(this);
        dialog.startDialog();
    }

    public void addAndSetUser(String user){
        dbHandler.addAndSetConfigUser(user);
        setHeader();
        resetPass();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.exit_double_press, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }

    private void parseAndAddEntry(MotionEvent ev){
        String msg;
        msg = String.valueOf(ev.getRawX());
        dbHandler.addDataEntry(msg);
    }

    boolean doublePatternTap = false;

    public void createNewPattern(View vw) {
        if (doublePatternTap) {
            //second tap - begin new pattern setup

            dbHandler.settings.put("PATTERN","");

            mViewDrawPath.clearPoints();
            mViewDrawPath.draw();
            adapter = new ViewGridAdapter(this,Math.floorDiv(sSide,3),"");
            radioGrid.setAdapter(adapter);

            setHeader();

            doublePatternTap = false;
            return;
        }
        int calibOption = dbHandler.checkProgressAndSetBestCalib(false);
        if (calibOption==-1) Toast.makeText(this, R.string.error_custom_pattern_primary_tests, Toast.LENGTH_LONG).show();
        else{
            this.doublePatternTap = true;
            Toast.makeText(this, R.string.custom_pattern_tap_twice, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doublePatternTap=false, 1000);
        }
    }

}
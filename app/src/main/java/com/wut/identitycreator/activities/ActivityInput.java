package com.wut.identitycreator.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.System.currentTimeMillis;


public class ActivityInput extends Activity implements SensorEventListener {

    GridView radioGrid;

    ViewDrawPath mViewDrawPath;

    DataDBHandler dbHandler;

    //mode = input OR calib
    String mode;

    private ViewGridAdapter adapter;
    private int sSide;

    private JSONArray data_entry;
    private SensorManager mSensorManager;

    private ArrayList<Sensor> sensors = new ArrayList<>();


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

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensors.add(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        sensors.add(mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        sensors.add(mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
        sensors.add(mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION));
        sensors.add(mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY));
        
        
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
    protected void onResume() {
        super.onResume();
        for (Sensor sensor : sensors){
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    private float[] mAccData = new float[3];
    private float[] mGyroData = new float[3];
    private float[] mRotVecData = new float[4];
    private float[] mLinAccData = new float[3];
    private float[] mGravData = new float[3];
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                mAccData = event.values.clone();
                break;
            case Sensor.TYPE_GYROSCOPE:
                mGyroData = event.values.clone();
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                mRotVecData = event.values.clone();
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                mLinAccData = event.values.clone();
                break;
            case Sensor.TYPE_GRAVITY:
                mGravData = event.values.clone();
                break;
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //  NOTHING
    }

    private JSONObject getSensorsData() throws JSONException{
        JSONObject sensorsData = new JSONObject();

        JSONObject sensor = new JSONObject();
        sensor.put("X",mAccData[0]);
        sensor.put("Y",mAccData[1]);
        sensor.put("Z",mAccData[2]);
        sensorsData.put("acc",sensor);

        sensor = new JSONObject();
        sensor.put("X",mGyroData[0]);
        sensor.put("Y",mGyroData[1]);
        sensor.put("Z",mGyroData[2]);
        sensorsData.put("gyro",sensor);

        sensor = new JSONObject();
        sensor.put("X",mRotVecData[0]);
        sensor.put("Y",mRotVecData[1]);
        sensor.put("Z",mRotVecData[2]);
        sensor.put("scalar",mRotVecData[3]);
        sensorsData.put("rotV",sensor);

        sensor = new JSONObject();
        sensor.put("X",mLinAccData[0]);
        sensor.put("Y",mLinAccData[1]);
        sensor.put("Z",mLinAccData[2]);
        sensorsData.put("linAcc",sensor);

        sensor = new JSONObject();
        sensor.put("X",mGravData[0]);
        sensor.put("Y",mGravData[1]);
        sensor.put("Z",mGravData[2]);
        sensorsData.put("grav",sensor);

        return sensorsData;
    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent ev){
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

    private long startTime;
    private long startTimeNano;

    private boolean handleInput(MotionEvent ev, float top){
        long time = System.nanoTime();
        if (ev.getAction()==MotionEvent.ACTION_DOWN){
            data_entry = new JSONArray();
            startTime = System.currentTimeMillis();
            startTimeNano = System.nanoTime();
        }

        if ((ev.getAction()==MotionEvent.ACTION_DOWN)||(ev.getAction()==MotionEvent.ACTION_MOVE)){
            ev.setAction(MotionEvent.ACTION_DOWN);
            super.dispatchTouchEvent(ev);
            ev.setAction(MotionEvent.ACTION_UP);
            boolean res = super.dispatchTouchEvent(ev);

            List<PointF> points = adapter.getSelected(top);
            points.add(new PointF(ev.getX(),ev.getY()-top));

            mViewDrawPath.resetPoints(points);
            mViewDrawPath.draw();

            JSONObject event = new JSONObject();
            try{
                event.put("time",System.nanoTime()-startTimeNano);

                JSONObject touch = new JSONObject();
                touch.put("x",ev.getRawX());
                touch.put("y",ev.getRawY());
                touch.put("tM",ev.getTouchMajor());
                touch.put("tm",ev.getTouchMinor());
                touch.put("s",ev.getSize());
                touch.put("p",ev.getPressure());
                event.put("tch",touch);
                event.put("sns",getSensorsData());
                //ToolMajor and ToolMinor are out-of-scope
                data_entry.put(event);
            }
            catch (JSONException e){
                e.printStackTrace();
                data_entry = new JSONArray();
            }

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
        JSONObject msg = new JSONObject();
        JSONObject header = new JSONObject();
        try {
            header.put("id",dbHandler.settings.get("UUID"));
            header.put("u",dbHandler.settings.get("USER"));
            header.put("ptn",dbHandler.settings.get("PATTERN"));

            JSONObject densityJSON = new JSONObject();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            densityJSON.put("xdpi",metrics.xdpi);
            densityJSON.put("ydpi",metrics.ydpi);
            header.put("dpi",densityJSON);

            header.put("c",adapter.calibrationSetting());

            header.put("tstamp",startTime);

            msg.put("header",header);
            msg.put("data",data_entry);
            dbHandler.addDataEntry(msg.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            data_entry = null;
        }
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
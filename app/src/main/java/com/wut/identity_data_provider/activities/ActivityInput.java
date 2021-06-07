package com.wut.identity_data_provider.activities;

import android.app.Activity;
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

import com.wut.identity_data_provider.R;
import com.wut.identity_data_provider.data.DataDBHandler;
import com.wut.identity_data_provider.dialogs.DialogInfo;
import com.wut.identity_data_provider.dialogs.DialogLoading;
import com.wut.identity_data_provider.dialogs.DialogUsers;
import com.wut.identity_data_provider.views.ViewDrawPath;
import com.wut.identity_data_provider.views.ViewGridAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ActivityInput extends Activity implements SensorEventListener {
    private final static String MODE_INPUT = "INPUT";
    private final static String MODE_CALIBRATION = "CALIBRATION";

    private String mMode; //INPUT or CALIBRATION
    private DataDBHandler mDBHandler;

    private int mPatternSquareSideWidth;
    private GridView mPatternGrid;
    private ViewGridAdapter mPatternGridAdapter;
    private ViewDrawPath mPatternDrawView;

    private JSONArray mInputPatternData;

    private SensorManager mSensorManager;
    private final ArrayList<Sensor> mSensors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // Block screen with loading dialog
        DialogLoading dialog = new DialogLoading(this);
        dialog.startDialog();

        // Initialize DB connection
        mDBHandler = new DataDBHandler(this);

        updateActivityHeader();

        // Input area calibration
        View view = findViewById(R.id.patternDomain);

        // Calculate the width of Grid Pattern Square
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int sOff = Math.floorDiv(width,10); // Offset from left and right to make it not to touch the sides of screen
        mPatternSquareSideWidth = width-2*sOff;

        // Check if it's the 1st launch
        if (Objects.equals(mDBHandler.mSettings.get(DataDBHandler.SETTING_CALIBRATION), "-1")) {
            mMode = MODE_CALIBRATION;

            int height = size.y - width;
            int hOff = Math.floorDiv(height,2);
            view.setPadding(sOff,(int)Math.round((hOff+sOff)/10.0)*10,sOff,0);

            View v = findViewById(R.id.INPUT_MODE);
            v.setLayoutParams(new RelativeLayout.LayoutParams(0, RelativeLayout.LayoutParams.MATCH_PARENT));

            v = findViewById(R.id.calibrationModeHeader);
            v.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        }
        else {
            mMode = MODE_INPUT;
            view.setPadding(sOff, Integer.parseInt(Objects.requireNonNull(mDBHandler.mSettings.get(DataDBHandler.SETTING_CALIBRATION))),sOff,0);
        }

        setHeaderMode();

        // Create an object of CustomAdapter and set Adapter to GirdView
        mPatternGrid = findViewById(R.id.radioGrid); // init GridView
        mPatternGridAdapter = new ViewGridAdapter(this,Math.floorDiv(mPatternSquareSideWidth,3), mDBHandler.mSettings.get(DataDBHandler.SETTING_PATTERN));

        mPatternGrid.setAdapter(mPatternGridAdapter);

        mPatternDrawView = findViewById(R.id.passPath);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensors.add(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        mSensors.add(mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        mSensors.add(mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
        mSensors.add(mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION));
        mSensors.add(mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY));
        
        
        //unblock screen after 2 seconds (initialization - getting x/y values for the grid points
        //there doesn't seem to eb
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            dialog.dismissDialog();
            if (Objects.equals(mDBHandler.mSettings.get(DataDBHandler.SETTING_CALIBRATION), "-1")) {
                startInfoDialog(findViewById(R.id.PW));
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (Sensor sensor : mSensors){
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
    public void onAccuracyChanged(Sensor sensor, int i) {}

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
            if (mMode.equals(MODE_INPUT)){
                return handleInput(ev,topHeight);
            }
            else if (mMode.equals(MODE_CALIBRATION)){
                handleCalibration(ev);
                return true;
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
        if (ev.getAction()==MotionEvent.ACTION_DOWN){
            mInputPatternData = new JSONArray();
            startTime = System.currentTimeMillis();
            startTimeNano = System.nanoTime();
        }

        if ((ev.getAction()==MotionEvent.ACTION_DOWN)||(ev.getAction()==MotionEvent.ACTION_MOVE)){
            ev.setAction(MotionEvent.ACTION_DOWN);
            super.dispatchTouchEvent(ev);
            ev.setAction(MotionEvent.ACTION_UP);
            boolean res = super.dispatchTouchEvent(ev);

            List<PointF> points = mPatternGridAdapter.getSelected(top);
            points.add(new PointF(ev.getX(),ev.getY()-top));

            mPatternDrawView.resetPoints(points);
            mPatternDrawView.draw();

            JSONObject event = new JSONObject();
            try{
                event.put("time",System.nanoTime()-startTimeNano);

                JSONObject touch = new JSONObject();
                touch.put("x",ev.getRawX());
                touch.put("y",ev.getRawY());
                // touch.put("tM",ev.getTouchMajor());
                // touch.put("tm",ev.getTouchMinor());
                // touch.put("s",ev.getSize());
                // touch.put("p",ev.getPressure());
                event.put("tch",touch);
                event.put("sns",getSensorsData());
                //ToolMajor and ToolMinor are out-of-scope
                mInputPatternData.put(event);
            }
            catch (JSONException e){
                e.printStackTrace();
                mInputPatternData = new JSONArray();
            }

            return res;
        }
        // When lifting finger - check passcode for validity and reset the buttons
        else if (ev.getAction()==MotionEvent.ACTION_UP){
            boolean res = mPatternGridAdapter.verifyResult();
            if (res) {
                if (Objects.equals(mDBHandler.mSettings.get(DataDBHandler.SETTING_PATTERN), "")){
                    ArrayList<Integer> newPattern = new ArrayList<>(mPatternGridAdapter.getAndClearInPasswd());
                    mDBHandler.addAndSetNewPattern(newPattern);

                    mDBHandler = new DataDBHandler(this);
                }
                else {
                    parseAndAddEntry(ev);
                }
                updateActivityHeader();
            }
            else {
                if (Objects.equals(mDBHandler.mSettings.get(DataDBHandler.SETTING_PATTERN), "")){
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

    private void handleCalibration(MotionEvent ev){
        View v = findViewById(R.id.patternDomain);
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

            TextView calibrationTests = findViewById(R.id.calibrationEntriesCount);
            int count = mDBHandler.completedTestsForCalibration(String.valueOf(topPadding));
            calibrationTests.setText(getResources().getQuantityString(R.plurals.completed_tests,count,count));

        }
    }

    private void resetPass(){
        mPatternDrawView.clearPoints();
        mPatternDrawView.draw();
        mPatternGridAdapter = new ViewGridAdapter(this,Math.floorDiv(mPatternSquareSideWidth,3), mDBHandler.mSettings.get(DataDBHandler.SETTING_PATTERN));
        mPatternGrid.setAdapter(mPatternGridAdapter);
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
        if (mMode.equals(MODE_CALIBRATION)){
            mMode = MODE_INPUT;
        }
        else {
            mMode = MODE_CALIBRATION;
        }
        setHeaderMode();
    }

    private void setHeaderMode(){
        View v;
        switch (mMode){
            case MODE_CALIBRATION:
                v = findViewById(R.id.INPUT_MODE);
                v.setLayoutParams(new RelativeLayout.LayoutParams(0, RelativeLayout.LayoutParams.MATCH_PARENT));

                v = findViewById(R.id.calibrationModeHeader);
                v.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                break;
            case MODE_INPUT:
                v = findViewById(R.id.INPUT_MODE);
                v.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT));

                v = findViewById(R.id.calibrationModeHeader);
                v.setLayoutParams(new RelativeLayout.LayoutParams(0, RelativeLayout.LayoutParams.MATCH_PARENT));
                break;
            default:
                break;
        }
    }


    public void switchMode(View view) {
        switchMode();
    }


    public void backFromCalibration(View view){
        if (Objects.equals(mDBHandler.mSettings.get(DataDBHandler.SETTING_CALIBRATION), "-1")){
            Toast.makeText(this,R.string.error_msg_first_calibration,Toast.LENGTH_SHORT).show();
        }
        else {

            View v = findViewById(R.id.patternDomain);
            v.setPadding(v.getPaddingLeft(), Integer.parseInt(Objects.requireNonNull(mDBHandler.mSettings.get(DataDBHandler.SETTING_CALIBRATION))),v.getPaddingRight(),0);

            resetPass();

            switchMode();
        }
    }

    public void saveCalibration(View view){
            View v = findViewById(R.id.patternDomain);
            mDBHandler.addAndSetCalibration(String.valueOf(v.getPaddingTop()));

            v.setPadding(v.getPaddingLeft(), Integer.parseInt(Objects.requireNonNull(mDBHandler.mSettings.get(DataDBHandler.SETTING_CALIBRATION))),v.getPaddingRight(),0);

            resetPass();
            switchMode();
            updateActivityHeader();
    }


    public void updateActivityHeader(){
        TextView v = findViewById(R.id.User);
        v.setText(mDBHandler.mSettings.get(DataDBHandler.SETTING_USER));
        v = findViewById(R.id.Pattern);
        String patternHeader = "";
        if (!Objects.equals(mDBHandler.mSettings.get(DataDBHandler.SETTING_PATTERN), "")){
            patternHeader = mDBHandler.mSettings.get(DataDBHandler.SETTING_PATTERN)+" ("+ mDBHandler.completedTests()+")";
        }
        v.setText(patternHeader);
        v = findViewById(R.id.calibrationEntriesCount);
        int count = mDBHandler.completedTestsForCalibration(mDBHandler.mSettings.get(DataDBHandler.SETTING_CALIBRATION));
        v.setText(getResources().getQuantityString(R.plurals.completed_tests,count,count));
    }

    public void patternLeft(View view) {
        int idx = mDBHandler.mPatterns.indexOf(mDBHandler.mSettings.get(DataDBHandler.SETTING_PATTERN));
        if (idx>0) idx--;
        else idx = mDBHandler.mPatterns.size()-1;
        mDBHandler.setConfigPattern(idx);
        updateActivityHeader();
        resetPass();
    }

    public void patternRight(View view) {
        int idx = mDBHandler.mPatterns.indexOf(mDBHandler.mSettings.get(DataDBHandler.SETTING_PATTERN));
        if (idx>= mDBHandler.mPatterns.size()-1) idx=0;
        else idx++;
        mDBHandler.setConfigPattern(idx);
        updateActivityHeader();
        resetPass();
    }

    public void startUserDialog(View view) {
        int hasGoodPattern = mDBHandler.checkProgressAndSetBestCalibration(true);
        if (hasGoodPattern!=-1) {
            View v = findViewById(R.id.patternDomain);
            v.setPadding(v.getPaddingLeft(), Integer.parseInt(Objects.requireNonNull(mDBHandler.mSettings.get(DataDBHandler.SETTING_CALIBRATION))),v.getPaddingRight(),0);
            DialogUsers dialog = new DialogUsers(this);
            dialog.startDialog(mDBHandler.mUsers);
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
        mDBHandler.addAndSetConfigUser(user);
        updateActivityHeader();
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
            header.put("id", mDBHandler.mSettings.get(DataDBHandler.SETTING_UUID));
            header.put("u", mDBHandler.mSettings.get(DataDBHandler.SETTING_USER));
            header.put("ptn", mDBHandler.mSettings.get(DataDBHandler.SETTING_PATTERN));

            JSONObject densityJSON = new JSONObject();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            densityJSON.put("xdpi",metrics.xdpi);
            densityJSON.put("ydpi",metrics.ydpi);
            header.put("dpi",densityJSON);

            header.put("c", mPatternGridAdapter.calibrationSetting());

            header.put("tstamp",startTime);

            msg.put("header",header);
            msg.put("data", mInputPatternData);
            mDBHandler.addDataEntry(msg.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            mInputPatternData = null;
        }
    }

    boolean doublePatternTap = false;

    public void createNewPattern(View vw) {
        if (doublePatternTap) {
            //second tap - begin new pattern setup

            mDBHandler.mSettings.put(DataDBHandler.SETTING_PATTERN,"");

            mPatternDrawView.clearPoints();
            mPatternDrawView.draw();
            mPatternGridAdapter = new ViewGridAdapter(this,Math.floorDiv(mPatternSquareSideWidth,3),"");
            mPatternGrid.setAdapter(mPatternGridAdapter);

            updateActivityHeader();

            doublePatternTap = false;
            return;
        }
        int calibrationOption = mDBHandler.checkProgressAndSetBestCalibration(false);
        if (calibrationOption==-1) Toast.makeText(this, R.string.error_custom_pattern_primary_tests, Toast.LENGTH_LONG).show();
        else{
            this.doublePatternTap = true;
            Toast.makeText(this, R.string.custom_pattern_tap_twice, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doublePatternTap=false, 1000);
        }
    }

}
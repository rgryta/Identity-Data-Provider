package com.wut.identitycreator.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PathView extends View {

    private Paint paint = new Paint();

    private List<PointF> pointFS = new ArrayList<PointF>();

    public PathView(Context context) {
        super(context);
    }

    public PathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onDraw(Canvas canvas){
        paint.setColor(Color.argb(168,128,128,255));
        paint.setStrokeWidth(50);

        if (pointFS.size()>1) canvas.drawLines(getLines(),paint);
        //canvas.drawLine(pointA.x,pointA.y,pointB.x,pointB.y,paint);

        super.onDraw(canvas);
    }

    public void resetPoints(List<PointF> pointFS){
        this.pointFS.clear();
        this.pointFS.addAll(pointFS);
    }

    public void clearPoints(){
        pointFS.clear();
    }

    private float[] getLines(){
        float[] pos = new float[(pointFS.size()-1)*4];
        for (int i=0;i<pointFS.size()-1;i++){
            pos[i*4]=pointFS.get(i).x;
            pos[i*4+1]=pointFS.get(i).y;
            pos[i*4+2]=pointFS.get(i+1).x;
            pos[i*4+3]=pointFS.get(i+1).y;
        }
        return pos;
    }

    public void draw(){
        invalidate();
        requestLayout();
    }
}

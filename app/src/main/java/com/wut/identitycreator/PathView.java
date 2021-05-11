package com.wut.identitycreator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class PathView extends View {

    private Paint paint = new Paint();
    private PointF pointA, pointB;

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
        paint.setStrokeWidth(40);

        canvas.drawLine(pointA.x,pointA.y,pointB.x,pointB.y,paint);

        super.onDraw(canvas);
    }

    public void setPointA(PointF pointA) {
        this.pointA = pointA;
    }

    public void setPointB(PointF pointB){
        this.pointB = pointB;
    }

    public void draw(){
        invalidate();
        requestLayout();
    }
}

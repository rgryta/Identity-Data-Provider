package com.wut.identity_data_provider.views;

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

/**
 * Class used for displaying the pattern lines while user is drawing.
 */
public class ViewDrawPath extends View {

    private final Paint paint = new Paint();

    private final List<PointF> pointFS = new ArrayList<>();

    /**
     * Mandatory constructors for View.
     *
     * @param context the context
     */
    public ViewDrawPath(Context context) {
        super(context);
    }

    /**
     * Instantiates a new View draw path.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public ViewDrawPath(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instantiates a new View draw path.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public ViewDrawPath(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * If more than one point is currently available then draw all lines between the points.
     *
     * @param canvas Canvas for drawing the lines.
     */
    @Override
    public void onDraw(Canvas canvas) {
        paint.setColor(Color.argb(168, 128, 128, 255));
        paint.setStrokeWidth(50);

        if (pointFS.size() > 1) canvas.drawLines(getLines(), paint);

        super.onDraw(canvas);
    }

    /**
     * Reset the point array with a new one.
     *
     * @param pointFS Array of all selected points + additional point of current fingertip position.
     */
    public void resetPoints(List<PointF> pointFS) {
        this.pointFS.clear();
        this.pointFS.addAll(pointFS);
    }

    /**
     * Method used for clearing the array with points to draw.
     */
    public void clearPoints() {
        pointFS.clear();
    }

    /**
     * Method used to calculate all the point positions to draw the lines.
     */
    private float[] getLines() {
        float[] pos = new float[(pointFS.size() - 1) * 4];
        for (int i = 0; i < pointFS.size() - 1; i++) {
            pos[i * 4] = pointFS.get(i).x;
            pos[i * 4 + 1] = pointFS.get(i).y;//-40;
            pos[i * 4 + 2] = pointFS.get(i + 1).x;
            pos[i * 4 + 3] = pointFS.get(i + 1).y;//-40;
        }
        return pos;
    }

    /**
     * Method used to invoke drawing.
     */
    public void draw() {
        invalidate();
        requestLayout();
    }
}

package com.huami.watch.gps.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import com.hs.gpxparser.modal.Waypoint;
import com.hs.gpxparser.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinliang on 17/3/7.
 */

public class DrawingWithoutBezier extends View {
    
     private static final String TAG = DrawingWithoutBezier.class.getSimpleName();

    private Paint gpxPaint = new Paint();
    private final Path mPath = new Path();

    private List<Waypoint> wayPoints ;

    public DrawingWithoutBezier(Context context) {
        super(context);
        LogUtils.print(TAG, "DrawingWithoutBezier");
        gpxPaint.setAntiAlias(true);
        gpxPaint.setStyle(Paint.Style.STROKE);
        gpxPaint.setStrokeWidth(10);
        gpxPaint.setColor(Color.RED);
        wayPoints = new ArrayList<>();
    }


    /**
     * 设置数据信息
     */
    public void setWayPointData(List<Waypoint> addData){
        this.wayPoints.addAll(addData);
        /**
         * 先是简单的绘制图案
         */
        for (Waypoint wayPoint : wayPoints) {
            //
            mPath.moveTo((float) wayPoint.getLatitude(), (float) wayPoint.getLongitude());
        }

        invalidate();


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        LogUtils.print(TAG, "onLayout");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        LogUtils.print(TAG, "onMeasure");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtils.print(TAG, "onDraw");
        canvas.drawPath(mPath, gpxPaint);


    }
}

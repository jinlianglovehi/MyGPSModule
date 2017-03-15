package com.huami.watch.gps.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import com.hs.gpxparser.utils.LogUtils;
import com.huami.watch.gps.utils.GPSUnitUtils;

/**
 * Created by jinliang on 17/3/8.
 */

public class TrailRouteView extends View {


     private static final String TAG = TrailRouteView.class.getSimpleName();



    // ######## 轨迹 track 绘制 info 信息 ###############
    /**
     * 轨迹 画笔
     */
    private Paint trackPaint ;

    /**
     *  mPath 路径信息
     */
    private final Path mPath = new Path();


    // ########## 绘制  地图
    /**
     * 地图轨迹的paint info
     */
    private  Paint mMapPaint ;

    // mMapPath 绘制轨迹
    private final Path mMapPath =new Path() ;

    public TrailRouteView(Context context) {
        super(context);
        initPaint();
        LogUtils.print(TAG, "TrailRouteView");
        initMapPaint();

    }

    private void initPaint(){
        LogUtils.print(TAG, "initPaint");
        trackPaint = new Paint();
        trackPaint.setAntiAlias(true);
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(10);
        trackPaint.setColor(Color.RED);
    }


    /**
     * 初始化 地图的 轨迹的 paint
     */
    private void initMapPaint(){

        mMapPaint= new Paint();
        mMapPaint.setAntiAlias(true);
        mMapPaint.setStyle(Paint.Style.STROKE);
        mMapPaint.setStrokeWidth(5);
        mMapPaint.setColor(Color.GREEN);


    }
  

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtils.print(TAG, "onDraw");
        // 绘制画图的坐标的轨迹范围
        canvas.drawRect(GPSUnitUtils.containerLeft,// 左侧点坐标
                 GPSUnitUtils.containerTop, // top 点坐标
                (GPSUnitUtils.containerLeft+GPSUnitUtils.containerWidth), // 计算右侧 x的位置
                (GPSUnitUtils.containerTop+GPSUnitUtils.containerHeight) ,// 计算 右侧 y 位置
                trackPaint);
        // x, y .r .paint
        canvas.drawCircle(GPSUnitUtils.containerLeft+GPSUnitUtils.containerWidth/2 ,
                GPSUnitUtils.containerTop+ GPSUnitUtils.containerHeight/2,2,trackPaint);

        // 绘制轨迹
        canvas.drawPath(mMapPath, mMapPaint);

    }


    /**
     * 添加 一个点的信息
     * @para  and longitude 经度   Latitude  维度
     * @param
     */
    // x 代表经度 ， y 代表维度
    public void addPoint( float lng, float  lat ){

        //
        float  x  = GPSUnitUtils.converLngToX(lng);
        float  y  = GPSUnitUtils.converLatToY(lat)  ;
        mMapPath.lineTo(x,y);
        postInvalidate();
    }


}

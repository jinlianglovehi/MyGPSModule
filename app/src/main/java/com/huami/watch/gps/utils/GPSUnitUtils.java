package com.huami.watch.gps.utils;

/**
 * Created by jinliang on 17/3/7.
 */

import android.location.Location;

import com.hs.gpxparser.utils.LogUtils;

/**
 * GPS 经纬度和屏幕之间的尺寸像素点之间的转换
 */
public class GPSUnitUtils {

     private static final String TAG = GPSUnitUtils.class.getSimpleName();

    /**
     * canvans 屏幕的控制
     */

    public static int containerWidth = 200 ; // 划线宽

    public static int containerHeight  = 200 ;// 划线高

    public static int containerLeft  = 0 ; // 矩形左上角 left 位置

    public static  int containerTop = 0 ;// 矩形做 左上角 top 位置



    /**
     * 获取单位distance 根据经纬度
     * @param lat
     * @param lng
     * @return
     */

    private static float mDistancePerLat = 0f ;

    private static float mDistancePerLng = 0f ;

    private static int offsetDistance = 100 ;// 设定 中心点位置偏移100

    private static int currentirectionD = DirectionType.North;


    /**
     * 设置中心点的坐标以 经纬度转换
     */
    private static float centerX = 116f ;// 中心点x

    private static float centerY = 40f ;// 中心点Y


    /**
     * 重新初始化经纬度的距离
     * @param lat
     * @param lng
     */
    public static void initDistanceByLonUtils(double lat, double lng){
        float[] w = new float[1];
        Location.distanceBetween(lat, lng, lat + 1, lng, w);
         mDistancePerLat = w[0];
        Location.distanceBetween(lat, lng, lat, lng + 1, w);
         mDistancePerLng = w[0];
        LogUtils.print(TAG, "getDistanceByLonUtils: mDistancePerLat:"+mDistancePerLat +""+",mDistancePerLng:"+mDistancePerLng);
    }


    /**
     * 获取左上角点的经纬度
     * @param currnetLat  当前的维度
     * @param currentLng  当前的经度
     * @return
     */
    public static float[] getLeftTopPoint(double currnetLat,double currentLng){
        float[] point = new float[2];

        initDistanceByLonUtils(currnetLat,currentLng);
//        mDistancePerLat * currnetLat + offsetDistance;


        return null;

    }


    /**
     * 转换 经度 to 屏幕个 x 点位置
     * @param lng
     * @return
     */
    public static float  converLngToX(float lng ){
        // TODO: 17/3/9

        return lng ;
    }

    /**
     * 转换 维度 to 屏幕的y 点坐标
     * @param lat
     * @return
     */
    public static float  converLatToY(float lat){
        // TODO: 17/3/9
        return lat ;
    }


    /**
     * 获取屏幕虚拟像素点的长度
     *
     * 1 个像素点 代表的虚拟长度
     * @param screenX
     * @return
     */
    public static float getRateScreenVirtualLen(float screenX){
        float rata = 2* offsetDistance / containerWidth ;
        return  rata ;
    }


    /**
     *
     *  根据中心点的经度 获取 图框范围的 经度的范围
     *
     *  @param  lon : 经度
     *
     *  @param  lat : 维度
     * @return
     */
    public static float[] getLanScopeByCenterLan(float lon  ,float lat ){

        float[] lanScope = new float[2] ;

//        initDistanceByLonUtils(lan);


        return null;
    }


    /**
     *  根据 中心点的 维度 获取 图框范围内的 维度范围
     * @param lat
     * @return
     */
    public static float[] getLatScopeByCenterLat(float lat){


        return null;
    }



}

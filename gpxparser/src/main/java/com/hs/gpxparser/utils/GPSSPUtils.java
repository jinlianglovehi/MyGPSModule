package com.hs.gpxparser.utils;

/**
 * Created by jinliang on 17/3/15.
 */

import android.content.Context;
import android.content.SharedPreferences;

/**
 * GPS SP 工具类使用
 */
public class GPSSPUtils {


    private static final String SP_GPS_ROUTE = "sp_gps_route" ;

    /**
     * 是否是第一次使用
     */
    private static final String IS_FIRST_USED = "is_first_used";


    /**
     * 当前选定的gpx 轨迹
     */
    public static final String SELECTED_GPX_ROUTE_NAME = "selected_gpx_route_name" ;


    /**
     * 当期选定的gpx 轨迹的文件名字
     */
    public static final String SELECTED_GPX_ROUTE_FILE_NAME = "select_gpx_route_file_name" ;



    /**
     * 设置 gps 轨迹使用的状态
     * @param mContext
     * @param
     */
    public static void  setGPSRouteUsedStatus(Context mContext ,boolean isFirstUserd){
        SharedPreferences mPreferenceBlue = mContext.getSharedPreferences(SP_GPS_ROUTE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mPreferenceBlue.edit();
        edit.putBoolean(IS_FIRST_USED,isFirstUserd);
        edit.commit();
    }


    /**
     *  获取
     * @param mContext
     * @return
     */
    public static boolean getGPSRouteIsNotFirstUsed(Context mContext){

        SharedPreferences mPreferenceBlue = mContext.getSharedPreferences(SP_GPS_ROUTE,
                Context.MODE_PRIVATE);
        boolean isFirstUsedStatus = mPreferenceBlue.getBoolean(IS_FIRST_USED,false);
        return isFirstUsedStatus;

    }


    /**
     * 设置 当前选定的轨迹的名称
     * @param mContext
     * @return
     */
    public static void setCurrentGPXRoute(Context mContext ,String currentGPXName,String selectedGPXFile){

        SharedPreferences mPreferenceBlue = mContext.getSharedPreferences(SP_GPS_ROUTE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mPreferenceBlue.edit();

        edit.putString(SELECTED_GPX_ROUTE_NAME,currentGPXName);

        edit.putString(SELECTED_GPX_ROUTE_FILE_NAME,selectedGPXFile);

        edit.commit();

    }


    /**
     *  返回的是 当前选择的轨迹的信息
     *
     * @param mContext
     *
     *  string 0 ：代表 routeName // 轨迹的名称
     *
     *  string 1 : 代表 routeFileName // 轨迹的文件的名称。
     *
     * @return
     */

    public static String[] getCurrentSelectedGPXRoute(Context mContext){

        SharedPreferences mPreferenceBlue = mContext.getSharedPreferences(SP_GPS_ROUTE,
                Context.MODE_PRIVATE);

        String[] gpxContent= new String[2];

        gpxContent[0] = mPreferenceBlue.getString(SELECTED_GPX_ROUTE_NAME,null);

        gpxContent[1] = mPreferenceBlue.getString(SELECTED_GPX_ROUTE_FILE_NAME,null);

        return gpxContent ;

    }


}

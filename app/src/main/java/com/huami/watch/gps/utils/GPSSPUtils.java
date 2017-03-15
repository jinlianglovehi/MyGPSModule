package com.huami.watch.gps.utils;

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


}

package com.hs.gpxparser.utils;

import android.util.Log;

/**
 * Created by jinliang on 17/1/20.
 */

public class LogUtils {

    private static final String  SEPARATION = "-------";
    private static final String TAG = LogUtils.class.getSimpleName();
    private static final boolean IS_DEBUG = true ;

    /**
     * android 平台log
     * @param tag
     * @param method
     * @param data
     */
    public static void print(String tag ,String method ,String data){

        if(IS_DEBUG){
            Log.i(TAG, new StringBuilder(tag).append(SEPARATION)
                    .append(method).append(SEPARATION)
                    .append(data).append(SEPARATION)
                    .toString());
        }
    }

    /**
     * android 平台log
     * @param tag
     * @param method
     */
    public static void print(String tag, String method ){
        if(IS_DEBUG){
            Log.i(TAG, new StringBuilder(tag).append(SEPARATION)
                    .append(method).append(SEPARATION)
                    .toString());

        }

    }


    /**
     * 打印 基本平台 Log
     * @param tag
     * @param data
     */
    public static void sysPrint(String tag ,String data){

        System.out.println(new StringBuilder("["+tag+"]---------").append(data)) ;
    }

}

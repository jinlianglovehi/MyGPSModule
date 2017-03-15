package com.hs.gpxparser.utils;

/**
 * Created by jinliang on 17/3/10.
 */

import com.hs.gpxparser.GPXParser;
import com.hs.gpxparser.modal.GPX;
import com.hs.gpxparser.modal.Route;
import com.hs.gpxparser.modal.Track;
import com.hs.gpxparser.modal.TrackSegment;
import com.hs.gpxparser.modal.Waypoint;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 *  GPS 数据处理工具类 - 最外层
 */
public class GPSDataUtils {

    private static final String TAG = GPSDataUtils.class.getSimpleName();
    public static GPSDataUtils instance ;

    // 设置 debug 是否打印 解析后的GPS数据点信息
    public static boolean isDebug = false ;


    public static GPSDataUtils getInstance (){
        if(instance==null){
            synchronized (GPSDataUtils.class){
                if(instance==null){
                    instance = new GPSDataUtils();
                }
            }
        }
        return instance;
    }


    /**
     * 获取 GPS 路点信息
     * @param inputStream
     * @return
     */
    public static HashSet<Waypoint>  getWayPointFromGPXFile(InputStream inputStream){

        GPXParser gpxParser = new GPXParser();
        HashSet<Waypoint> set =null ;

        try {
            GPX gpx = gpxParser.parseGPX(inputStream);
            set =  gpx.getWaypoints();
            LogUtils.print(TAG, "testGpxCompleteFile Size:"+ set.size());
            // 打印 GPS 数据点log 信息
            printWayPoints(set);
        } catch (Exception e) {
            LogUtils.print(TAG, "getWayPointFromGPXFile is error ");
            e.printStackTrace();
        }
        return set ;
    }


    /**
     * 获取 trackSegment  Track 路段信息
     * @param
     */

    public static HashSet<Track> getTrackSegsFromGPXInputStream(InputStream inputStream){

        GPXParser gpxParser  =new GPXParser();
        HashSet<Track>  trackSet  = null ;
        try {
            GPX  gpx = gpxParser.parseGPX(inputStream);
            trackSet = gpx.getTracks();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trackSet ;
    }


    /**
     *  通过 track 获取 这个 track 中的所有路点
     *  其中这个文件中 只有唯一的一个 trackSegment
     * @param track
     * @return
     */
    public static List<Waypoint> getWayPointFromTrack(Track track){

        ArrayList<TrackSegment>  trackSegs =  track.getTrackSegments();

        if(trackSegs!=null && trackSegs.size()> 0){
           return  trackSegs.get(0).getWaypoints();
        }else{
            return null ;
        }

    }


    /**v版本
     *  获取 Routes 信息 从 gpx 文件中
     * @param inputStream
     */
    public static HashSet<Route> getRoutesFromGPXInputStream(InputStream inputStream){

        GPXParser gpxParser  =new GPXParser();
        HashSet<Route>  set =null  ;
        try {
            GPX  gpx = gpxParser.parseGPX(inputStream);
            set  = gpx.getRoutes();
        } catch (Exception e) {
            LogUtils.print(TAG, "getRoutesFromGPXInputStream is error ");
            e.printStackTrace();
        }
        return set ;
    }

    // #####  日志 信息的 打印  ##########
    /**
     * 打印 wayPoint 数据点信息
     */
    private static void  printWayPoints(HashSet<Waypoint> set){

        if(isDebug){
            // 点集合是没有顺序的集合数据点
            Iterator<Waypoint> iterator = set.iterator() ;
            Waypoint currentPoint  ;
            while (iterator.hasNext()){
                currentPoint = iterator.next();
                LogUtils.print(TAG, "testGpxCompleteFile:"+ currentPoint.toString());
            }
        }

    }



}

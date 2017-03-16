package com.hs.gpxparser.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;

import com.hs.gpxparser.GPXParser;
import com.hs.gpxparser.appmodel.GPXAppRoute;
import com.hs.gpxparser.modal.GPX;
import com.hs.gpxparser.modal.Waypoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinliang on 17/3/13.
 *
 * 专门 针对的是 GPS 操作sdcard 的工具类
 */

public class SDCardGPSUtils {

    /**
     * 运动路线的存放的位置
     */
    public static final String filePath = "/sdcard/.sportroute/gpxdata/";

    /**
     * 根据轨迹生成gpx 文件的信息
     */
    public static final String ROUTE_IMG_PATH = "/sdcard/.sportroute/gpximg/" ;

    public static final String gpxFileFormat = ".gpx" ;

    public static final String IMG_FILE_FORMAT = ".png" ;

    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }


    /**
     * 根据gpx 轨迹绘制生成一张图片
     * @return
     */
    public static  File[] getGPSSDCardFiles(){

        if(isSdCardExist()){
            File currentPath = new File(filePath);
            if(currentPath.exists() && currentPath.isDirectory()){
                File[] gpxFiles = currentPath.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        if (filename != null && filename.endsWith(gpxFileFormat)) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                return gpxFiles ;
            }else {
                return null;
            }
        }
        return  null ;
    };


    /**
     * 根据路点轨迹 生成一个图片的位置的信息
     * @param listPoint 轨迹中的路点的集合
     * @param outImgPath 输出的外部的地图的位置
     *
     */
    public void productImgFromGPSRoutes(List<Waypoint> listPoint,String outImgPath){

        Bitmap bm = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        Paint p = new Paint();
        canvas.drawRect(50, 50, 200, 200, p);
        canvas.save(Canvas.ALL_SAVE_FLAG );
        canvas.restore();

        // TODO: 17/3/13 绘制轨迹地图 start






        // 绘制轨迹地图 end

        

        // 将文件输出到指定位置
        StringBuilder sb = new StringBuilder();
        sb.append(ROUTE_IMG_PATH);
        sb.append(outImgPath);
        sb.append(IMG_FILE_FORMAT);

        File f = new File(sb.toString());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 50, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }


    /**
     * 获取的时候当前sdcard 上的全部的gpx file 的信息
     * @return
     */
    public static List<GPXAppRoute> getGPXAppRouteFromSDCard(){

        File[] files = getGPSSDCardFiles();

        // TODO: 17/3/13  
        // 返回的是数据
        List<GPXAppRoute> gpxAppRoutes = new ArrayList<>();

        GPXAppRoute gpxAppRoute = null ;

        GPXParser gpxParser = new  GPXParser();
        for (File currentFile: files) {
            gpxAppRoute  = new GPXAppRoute();

            try {
                GPX gpx =  gpxParser.parseSimpleGPX(new FileInputStream(filePath+currentFile));
                gpxAppRoute.setRouteName(gpx.getMetadata().getName());// 设置轨迹的名称
                // 设置传输完毕后生成的轨迹缩图的filePath
                gpxAppRoute.setRouteImgPath(getGPXRouteImagePath(currentFile.getName()));
                // 根据轨迹计算的 轨迹长度
                gpxAppRoute.setRouteLength(0f);
                // 添加一个 gpx app route 轨迹的集合
                gpxAppRoutes.add(gpxAppRoute);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        return gpxAppRoutes;


    }

    /**
     *  根据文件 获取 GPX 点的数据
     */
    public static GPX getWayPointsFromFileName(String fileName){

        GPXParser gpxParser =new GPXParser();

        GPX gpx = null ;
        try {
            gpx =  gpxParser.parseGPX(new FileInputStream(filePath +fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return gpx;
    }



    /**
     * 通过文件的名称 获取 gpx route轨迹的图片的sdcard filepath
     * @param fileName ;以gpx 文件的名称作为唯一的标记点
     * @return
     */
    public static String getGPXRouteImagePath(String fileName){
        StringBuilder sb = new StringBuilder();
        sb.append(ROUTE_IMG_PATH);
        sb.append(fileName);
        sb.append(IMG_FILE_FORMAT);
        return sb.toString();
    }

}

package com.huami.watch.gps;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.hs.gpxparser.GPXParser;
import com.hs.gpxparser.modal.GPX;
import com.hs.gpxparser.modal.Track;
import com.hs.gpxparser.modal.Waypoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jinliang on 17/3/6.
 */


@RunWith(AndroidJUnit4.class)

public class TestGpxFileParser  {


     private static final String TAG = TestGpxFileParser.class.getSimpleName();
    private Context appContext ;
    @Before
    public void getAppContext(){
        appContext = InstrumentationRegistry.getTargetContext();
    }


    /**
     *  后去轨迹的点坐标
     */

//    @Test
    public  void testParSer(){

        LogUtils.print(TAG, "testParSer");

        GPXParser  gpxParser = new GPXParser();

        try {
            GPX gpx  =gpxParser.parseGPX(appContext.getAssets().open("TNF100_Beijing_100km.gpx"));

            LogUtils.print(TAG, "testParSer size:"+ gpx.getTracks());

            if(gpx.getTracks().size()>0){
                LogUtils.print(TAG, "testParSer size > 0");

               Iterator<Track>  trackIterator  =gpx.getTracks().iterator();

                while (trackIterator.hasNext()){
                    Track track =trackIterator.next();

                   List<Waypoint> wayPonits =  track.getTrackSegments().get(0).getWaypoints();

                    for (Waypoint wayPoint : wayPonits
                         ) {
                        LogUtils.print(TAG, "testWayPoint:"+ wayPoint.toString());

                    }

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    /**
     * 英里到公里
     */
    public static final float MI_TO_KM = 1.609344f;
    /**
     * 公里到英里
     */
    public static final float KM_TO_MI = 0.621371192237f;


    @Test
    public  void testMIsToKm(){
        String result = "10公里" ;

        Pattern compile = Pattern.compile("\\d+");
        Matcher matcher = compile.matcher(result);

        boolean ishasNumber =  matcher.find();

        System.out.println("ishasNumber："+ ishasNumber); // 4.8

        if(ishasNumber){


        String string = matcher.group();//提取匹配到的结果
        double km = Double.valueOf(string);
        double miles = km * KM_TO_MI ;
        String finalResult = String.format("%.1f",miles);

        String title = result.replaceAll(string,finalResult).toString();

         // 分支判断 是否要替换km

        title =  title.replace("km","公里").toString();

        System.out.println(title); // 4.8
        }


    }

}

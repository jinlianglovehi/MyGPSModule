package com.huami.watch.gps;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.hs.gpxparser.GPXParser;
import com.hs.gpxparser.modal.GPX;
import com.hs.gpxparser.modal.Waypoint;
import com.hs.gpxparser.utils.LogUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by jinliang on 17/3/10.
 */


@RunWith(AndroidJUnit4.class)
public class TestGpXCompleteFileParser {

     private static final String TAG = TestGpXCompleteFileParser.class.getSimpleName();
    private Context appContext ;

    private final String fileName = "garmin.gpx" ;



    @Before
    public void getAppContext(){
        appContext = InstrumentationRegistry.getTargetContext();
    }


    @Test
    public void testGpxCompleteFile() {

        GPXParser gpxParser = new GPXParser();
        try {
            GPX gpx = gpxParser.parseGPX(appContext.getAssets().open(fileName));
            HashSet<Waypoint> set =  gpx.getWaypoints();

            LogUtils.print(TAG, "testGpxCompleteFile Size:"+ set.size());

            // 点集合是没有顺序的集合数据点
            Iterator<Waypoint> iterator = set.iterator() ;
            Waypoint currentPoint  ;
            while (iterator.hasNext()){
                currentPoint = iterator.next();
                LogUtils.print(TAG, "testGpxCompleteFile:"+ currentPoint.toString());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

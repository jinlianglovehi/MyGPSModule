package com.huami.watch.gps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.hs.gpxparser.GPXParser;
import com.hs.gpxparser.appmodel.GPXAppRoute;
import com.hs.gpxparser.modal.GPX;
import com.hs.gpxparser.modal.Track;
import com.hs.gpxparser.modal.Waypoint;
import com.hs.gpxparser.utils.LogUtils;
import com.hs.gpxparser.utils.SDCardGPSUtils;
import com.huami.watch.gps.utils.IResultCallBack;
import com.huami.watch.gps.utils.RxUtils;
import com.huami.watch.gps.view.DrawingWithoutBezier;
import com.huami.watch.gps.view.TrailDrawer;
import com.huami.watch.gps.view.TrailRouteView;

import java.io.File;
import java.sql.Time;
import java.util.Iterator;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawingWithoutBezier drawingWithoutBezier;

    private List<Waypoint> listData;

    private TrailRouteView trainRouteView;

    private ImageView guiji;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        drawingWithoutBezier = new DrawingWithoutBezier(this);
//
//        trainRouteView = new TrailRouteView(this);
//        setContentView(trainRouteView);

        guiji = (ImageView) findViewById(R.id.guiji);


        initData();
    }

    private void initData() {

        RxUtils.operate(new Observable.OnSubscribe<List<Waypoint>>() {
            @Override
            public void call(Subscriber<? super List<Waypoint>> subscriber) {

//                initAddData();
                LogUtils.print(TAG, "call");
//                List<Waypoint> listData = getFileGPSData(MainActivity.this);
                initAddData() ;
                subscriber.onNext(listData);
                subscriber.onCompleted();


            }
        }, new IResultCallBack<List<Waypoint>>() {
            @Override
            public void onSuccess(List<Waypoint> waypoints) {

                LogUtils.print(TAG, "onSuccess draw line ");
                drawGuiJi(waypoints);



            }

            @Override
            public void onFail(List<Waypoint> waypoints, String msg) {

            }
        });


    }


    /**
     * 测试获取 轨迹 from sdcard 中
     */
    private void testGetRouteFromSDCard(){
        
        List<GPXAppRoute> gpxRoutes =  SDCardGPSUtils.getGPXAppRouteFromSDCard();
        for (GPXAppRoute route: gpxRoutes ) {

            LogUtils.print(TAG, "testGetRouteFromSDCard:"+ route.toString());
        }

    }



    /**
     * 测试 获取文件的地址
     */
//    private List<Waypoint> getFileGPSData(Context mContext) {
//
//        LogUtils.print(TAG, "getFileGPSData");
////        File[] files = SDCardGPSUtils.getGPSSDCardFiles();
//
////        LogUtils.print(TAG, "getFileGPSData fileSize:" + files.length);
////        for (File file : files) {
////            LogUtils.print(TAG, "getFileGPSData fileName:" + file.getName());
////
////        }
////
//////        开始解析
////        LogUtils.print(TAG, "getFileGPSData start Parser  :" + System.currentTimeMillis() );
////        GPX gpx = SDCardGPSUtils.getWayPointsFromFileName(files[0].getName());
//
//        // 结束解析
//
//        GPX gpx  =gpxParser.parseGPX(MainActivity.this.getAssets().open("TNF100_Beijing_100km.gpx"));
//
//        LogUtils.print(TAG, "getFileGPSData end Parser :"+System.currentTimeMillis() );
//        List<Waypoint> listData = printTrackPoint(gpx,files[0].getName());
//
//        return listData ;
//
//    }


    private List<Waypoint> printTrackPoint(GPX gpx,String fileName) {


        LogUtils.print(TAG, "printTrackPoint");
        LogUtils.print(TAG, "printTrackPoint trackSize:"+ gpx.getTracks());
        Iterator<Track> trackIterator = gpx.getTracks().iterator();

        List<Waypoint> listData = null ;
        while (trackIterator.hasNext())

        {
            Track track = trackIterator.next();

             listData = track.getTrackSegments().get(0).getWaypoints();

        }

        return listData ;


    }





    private TrailDrawer mTrailDrawer ;

    private void drawGuiJi(List<Waypoint> listData){
        mTrailDrawer = new TrailDrawer(MainActivity.this, 200, 200, true);
        mTrailDrawer.startDraw();
        mTrailDrawer.setStart(R.drawable.sport_start_route);
        mTrailDrawer.setEnd(R.drawable.sport_history_end_route);
//        mTrailAnimView.setStart(R.drawable.sport_start_route);
//        mTrailAnimView.setEnd(R.drawable.sport_history_end_route);
        mTrailDrawer.addLocationDatasToBitmap(listData);
        final Bitmap bitmap = mTrailDrawer.newTrailBitmap();
        guiji.setImageDrawable(new BitmapDrawable(getResources(), bitmap));

        SDCardGPSUtils.saveBitmapToSDCard(bitmap,"TNF100_Beijing_100km");
    }


    private void initAddData(){

        GPXParser gpxParser = new GPXParser();
        try {
            GPX gpx  =gpxParser.parseGPX(MainActivity.this.getAssets().open("TNF100_Beijing_100km.gpx"));

            LogUtils.print(TAG, "testParSer size:"+ gpx.getTracks());

            if(gpx.getTracks().size()>0){
                LogUtils.print(TAG, "testParSer size > 0");

                Iterator<Track> trackIterator  =gpx.getTracks().iterator();

                while (trackIterator.hasNext()){
                    Track track =trackIterator.next();

                    listData =  track.getTrackSegments().get(0).getWaypoints();

                    for (Waypoint wayPoint : listData) {

                         LogUtils.print(TAG, "initAddData lng:"+ wayPoint.getLongitude() +",lat:"+ wayPoint.getLatitude());
                         trainRouteView.addPoint((float)wayPoint.getLongitude(),
                                 (float) wayPoint.getLatitude());
                    }

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

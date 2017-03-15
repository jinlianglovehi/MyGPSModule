package com.huami.watch.gps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hs.gpxparser.GPXParser;
import com.hs.gpxparser.modal.GPX;
import com.hs.gpxparser.modal.Track;
import com.hs.gpxparser.modal.Waypoint;
import com.hs.gpxparser.utils.LogUtils;
import com.huami.watch.gps.utils.IResultCallBack;
import com.huami.watch.gps.utils.RxUtils;
import com.huami.watch.gps.view.DrawingWithoutBezier;
import com.huami.watch.gps.view.TrailRouteView;

import java.util.Iterator;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class MainActivity extends AppCompatActivity {

     private static final String TAG = MainActivity.class.getSimpleName();

    private  DrawingWithoutBezier drawingWithoutBezier ;

    private List<Waypoint> listData  ;

    private  TrailRouteView trainRouteView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawingWithoutBezier  =new DrawingWithoutBezier(this);

        trainRouteView  =new TrailRouteView(this);
        setContentView(trainRouteView);

        initData();
    }

    private void initData(){

        RxUtils.operate(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {

                initAddData();
                subscriber.onNext(true);
                subscriber.onCompleted();


            }
        }, new IResultCallBack<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if(aBoolean==true){

                    drawingWithoutBezier.setWayPointData(listData);

                }

            }

            @Override
            public void onFail(Boolean aBoolean, String msg) {

            }
        });



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

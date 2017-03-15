package com.huami.watch.gps;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hs.gpxparser.utils.LogUtils;
import com.huami.watch.gps.utils.GPSSPUtils;

/**
 *
 *  使用运动轨迹的界面
 *
 *
 */


public class SportRouteActivity extends Activity  implements View.OnClickListener{


     private static final String TAG = SportRouteActivity.class.getSimpleName();


    /**
     * 点击 变大或者缩小的按钮
     */
    private TextView txtBecomeBigger ,txtBecomeSmaller ;

    private String currentNavigateRoute  = null ;

    private RelativeLayout routeLayout,toastLayout;

    private TextView confirmTxt,cancelTxt ;

    // 当前轨迹的title
    private String currnetRouteTitle  ="香山二号线"  ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_route_run);
//        initDialog();

        toggleShowContainer(R.id.route_container);

    }


    /**
     *  初始化dialog
     */
    private void initDialog(){

        LogUtils.print(TAG, "initDialog");
        routeLayout = (RelativeLayout) findViewById(R.id.route_container);
        toastLayout = (RelativeLayout) findViewById(R.id.rl_toast_container);

        initToastContainer();
    }

    /**
     *  弹出 toast 的容器
     */
    private void initToastContainer(){

        if(toastLayout!=null){
            confirmTxt = (TextView) findViewById(R.id.txt_confirm);
            cancelTxt = (TextView) findViewById(R.id.txt_cencel);


            ((TextView)findViewById(R.id.markd_desc)).setText(currnetRouteTitle);
            confirmTxt.setOnClickListener(SportRouteActivity.this);
            cancelTxt.setOnClickListener(SportRouteActivity.this);
            toastLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LogUtils.print(TAG, "onClick  toastLayout  ");
                    toggleShowContainer(R.id.route_container);
                }
            });




        }


    }

    /**
     * 初始化 轨迹的容器
     */
    private void initRouteContainer(){


    }

    private void toggleShowContainer(int containerId){

        switch (containerId){
            case R.id.route_container:
                findViewById(R.id.route_container).setVisibility(View.VISIBLE);
                findViewById(R.id.rl_toast_container).setVisibility(View.GONE);
                break;
            case R.id.rl_toast_container:
                findViewById(R.id.route_container).setVisibility(View.GONE);
                findViewById(R.id.rl_toast_container).setVisibility(View.VISIBLE);
                break;

        }

    }
    @Override
    public void onClick(View view) {

        LogUtils.print(TAG, "onClick id:");
         switch (view.getId()){

             case R.id.txt_confirm:
                 // 切换界面
                 LogUtils.print(TAG, "onClick  confirm ");

                 // gps 轨迹是否第一次使用
                 boolean gpsRouteIsNotFirstUsed = GPSSPUtils.getGPSRouteIsNotFirstUsed(SportRouteActivity.this);

                 LogUtils.print(TAG, "onClick gpsRouteIsNotFirstUsed:"+ gpsRouteIsNotFirstUsed);

                 if(gpsRouteIsNotFirstUsed){// 不是第一次使用
                     toggleShowContainer(R.id.route_container);
                 }else{// 第一次使用
                     firstUserRouteRemind();
                 }

                 break;
             case R.id.txt_cencel:
                 LogUtils.print(TAG, "onClick cencel ");
                 toggleShowContainer(R.id.route_container);

                 break;

             case R.id.remind_confirm:
                 LogUtils.print(TAG, "onClick  remindConfirm ");

                 GPSSPUtils.setGPSRouteUsedStatus(SportRouteActivity.this,false);
                 toggleShowContainer(R.id.route_container);

                 break;
         }
    }


    /**
     * 第一次使用轨迹时候的提醒
     */
    private void firstUserRouteRemind(){


        // 隐藏 是，否

        findViewById(R.id.txt_confirm).setVisibility(View.GONE);
        findViewById(R.id.txt_cencel).setVisibility(View.GONE);
        findViewById(R.id.middle_separtor_line).setVisibility(View.GONE);

        // 设置 confirm
        TextView  remindConfirm = (TextView) findViewById(R.id.remind_confirm);
        remindConfirm.setVisibility(View.VISIBLE);
        remindConfirm.setOnClickListener(SportRouteActivity.this);

        ((TextView) findViewById(R.id.markd_desc)).setText(getString(R.string.please_start_sport_from_route_start));


    }
}

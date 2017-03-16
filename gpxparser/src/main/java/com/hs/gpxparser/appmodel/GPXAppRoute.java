package com.hs.gpxparser.appmodel;

import java.io.Serializable;

/**
 * Created by jinliang on 17/3/13.
 *  GPX 文件中的建树的
 */
public class GPXAppRoute implements Serializable {


    /**
     * 生成轨迹的imgpath sdcard 地址
     */
    private String routeImgPath;

    /**
     * 轨迹的名称
     */
    private String routeName ;

    /**
     * 轨迹的长度
     */
    private float routeLength ;


    public String getRouteImgPath() {
        return routeImgPath;
    }

    public void setRouteImgPath(String routeImgPath) {
        this.routeImgPath = routeImgPath;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public float getRouteLength() {
        return routeLength;
    }

    public void setRouteLength(float routeLength) {
        this.routeLength = routeLength;
    }


    @Override
    public String toString() {
        return "GPXAppRoute{" +
                "routeImgPath='" + routeImgPath + '\'' +
                ", routeName='" + routeName + '\'' +
                ", routeLength=" + routeLength +
                '}';
    }
}

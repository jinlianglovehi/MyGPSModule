//package com.huami.watch.gps.view;
//
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.LinearGradient;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.PorterDuff;
//import android.graphics.Shader;
//import android.location.Location;
//import android.os.SystemClock;
//
//
//import com.huami.watch.gps.utils.LogUtils;
//
//import java.util.LinkedList;
//import java.util.List;
//
///**
// * Created by zhangfan on 16-5-10.
// */
//public class TrailBitmapProviderImpl implements ITrailBitmapProvider {
//    private static final String TAG = "TrailBitmapProviderImpl";
//    private static final int STATUS_STOPED = 0;
//    private static final int STATUS_STARTED = 1;
//    private static final float INVALID_LNG_LAT = 1800;
//    private static final float MAX_PIXELS_METER_RATIO = 2f;
//    private static final int DEFAULT_WIDTH = 196;
//    private static final int DEFAULT_HEIGHT = 196;
//    private static final int INNER_MARGIN = 30;
//    private IPointProvider mPointProvider = null;
//    private Bitmap mFixedPointBitmap = null;
//    private volatile Bitmap mAllPointListBitmap = null;
//    private volatile Bitmap mTmpTrailBitmap = null;
//    private boolean mWithMaxPixel = true;
//    private float mMinLng = INVALID_LNG_LAT;
//    private float mMaxLng = -INVALID_LNG_LAT;
//    private float mMinLat = INVALID_LNG_LAT;
//    private float mMaxLat = -INVALID_LNG_LAT;
//    private int DEFAULT_LINE_COLOR = 0xffff0000;
//    private int mWidth = 0;
//    private int mHeight = 0;
//    private float mDistancePerLat = -1;
//    private float mDistancePerLng = -1;
//    private int mDrawAreaWidth = DEFAULT_WIDTH - INNER_MARGIN;
//    private int mDrawAreaHeight = DEFAULT_HEIGHT - INNER_MARGIN;
//    private Canvas mFixedPointCanvas = null;
//    private Canvas mAllPointCanvas = null;
//    private Canvas mTmpTrailCanvas = null;
//    private float mPathLngDistance = -1;
//    private float mPathLatDistance = -1;
//    private int status = STATUS_STARTED;
//    private float mRatio = -1;
//    private float mDrawPathWidth = 0;
//    private float mDrawPathHeight = 0;
//    private float mXOffset = 0;
//    private float mYOffset = 0;
//    private float mWidthLngRatio = 0;
//    private float mHeightLatRatio = 0;
//    private static final int LINE_WIDTH = 4;
//    private Bitmap.Config mBitmapConfig = Bitmap.Config.ARGB_8888;
//    private Point mLastFixedPoint = null;
//    private Point mLastUpdatedPoint = null;
//    private LinearGradient mShader = null;
//    private volatile Paint mSportTrailPaint = null;
//    private Point mStartPoint = null;
//    private Point mEndPoint = null;
//    private Point mPenulTimatePoint = null;
//    private Bitmap mStartBitmap = null;
//    private Bitmap mEndBitmap = null;
//    private Matrix mMatrix = null;
//
//    public TrailBitmapProviderImpl(int width, int height,
//                                   Bitmap startBitmap, Bitmap endBitmap, boolean withMaxPixel) {
//        this(width, height, startBitmap, endBitmap);
//        mWithMaxPixel = withMaxPixel;
//    }
//
//    public TrailBitmapProviderImpl(int width, int height, Bitmap startBitmap, Bitmap endBitmap) {
//        mWidth = width;
//        mHeight = height;
//        mDrawAreaWidth = width - INNER_MARGIN;
//        mDrawAreaHeight = height - INNER_MARGIN;
//        mStartBitmap = Bitmap.createBitmap(startBitmap);
//        mEndBitmap = Bitmap.createBitmap(endBitmap);
//        mMatrix = new Matrix();
//    }
//
//    /**
//     * 初始化操作
//     * @param provider
//     */
//    @Override
//    public void start(IPointProvider provider) {
//        mSportTrailPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
//        mSportTrailPaint.setColor(DEFAULT_LINE_COLOR);
//        mSportTrailPaint.setStyle(Paint.Style.STROKE);
//        mSportTrailPaint.setStrokeWidth(LINE_WIDTH);
//        mSportTrailPaint.setStrokeCap(Paint.Cap.ROUND);
//
//        mPointProvider = provider;
//        if (mFixedPointBitmap != null) {
//            mFixedPointBitmap.recycle();
//            mFixedPointBitmap = null;
//        }
//        mFixedPointBitmap = Bitmap.createBitmap(mWidth, mHeight, mBitmapConfig);
//
//        mFixedPointCanvas = new Canvas(mFixedPointBitmap);
//        if (mAllPointListBitmap != null) {
//            mAllPointListBitmap.recycle();
//            mAllPointListBitmap = null;
//        }
//        mAllPointListBitmap = Bitmap.createBitmap(mWidth, mHeight, mBitmapConfig);
//        mAllPointCanvas = new Canvas(mAllPointListBitmap);
//        synchronized (this) {
//            if (mTmpTrailBitmap != null) {
//                mTmpTrailBitmap.recycle();
//                mTmpTrailBitmap = null;
//            }
//        }
//        mTmpTrailBitmap = Bitmap.createBitmap(mWidth, mHeight, mBitmapConfig);
//        mTmpTrailCanvas = new Canvas(mTmpTrailBitmap);
//        setStatus(STATUS_STARTED);
//    }
//
//    private void initDistancePerLatLng(float lat, float lng) {
//        if (mDistancePerLat < 0 || mDistancePerLng < 0) {
//
//            //
//            float[] w = new float[1];
//            Location.distanceBetween(lat, lng, lat + 1, lng, w);
//            mDistancePerLat = w[0];
//
//            Location.distanceBetween(lat, lng, lat, lng + 1, w);
//            mDistancePerLng = w[0];
//        }
//    }
//
//    /**
////     * 更新东西南北四个边界
//     */
//    private void updateBorder() {
//        mMaxLng = mPointProvider.getEast().mLongitude;
//        mMinLng = mPointProvider.getWest().mLongitude;
//        mMaxLat = mPointProvider.getNorth().mLatitude;
//        mMinLat = mPointProvider.getSouth().mLatitude;
//    }
//
//    /**
//     * 更新绘制比例：绘制长度/经纬度
//     * @param locationDatas 需要绘制的所有GPS点
//     */
//    private void updateRatio(List<? extends SportLocationData> locationDatas) {
//        if (Global.DEBUG_VIEW) {
//            LogUtils.sysPrint(TAG, "check and update " + locationDatas);
//        }
//        if (mWidth <= 0 || mHeight <= 0) {
//            if (Global.DEBUG_LEVEL_1) {
//                LogUtils.sysPrint(TAG, "invalid width or height. width : " + mWidth + ", height : " + mHeight);
//            }
//            return;
//        }
//        initDistancePerLatLng(locationDatas.get(0).mLatitude, locationDatas.get(0).mLongitude);
//
//        //
//        mPathLngDistance = mDistancePerLng * (mMaxLng - mMinLng);
//        mPathLatDistance = mDistancePerLat * (mMaxLat - mMinLat);
//        if (mWithMaxPixel &&
//                Math.max(mPathLngDistance, mPathLatDistance) <
//                        Math.min(mDrawAreaWidth, mDrawAreaHeight) / MAX_PIXELS_METER_RATIO) {
//            mRatio = MAX_PIXELS_METER_RATIO;
//        } else if (mPathLatDistance != 0 && mPathLngDistance == 0) {
//            mRatio = (float) mDrawAreaHeight / mPathLatDistance;
//        } else if (mPathLatDistance == 0 && mPathLngDistance != 0) {
//            mRatio = (float) mDrawAreaWidth / mPathLngDistance;
//        } else {
//            float actualRatio = mPathLngDistance / mPathLatDistance;
//            float viewRatio = (float) mDrawAreaWidth / mDrawAreaHeight;
//            if (actualRatio > viewRatio) {
//                mRatio = (float) mDrawAreaWidth / mPathLngDistance;
//            } else {
//                mRatio = (float) mDrawAreaHeight / mPathLatDistance;
//            }
//        }
//
//        mWidthLngRatio = mDistancePerLng * mRatio;
//        mHeightLatRatio = mDistancePerLat * mRatio;
//
//        mDrawPathWidth = (mMaxLng - mMinLng) * mWidthLngRatio;
//        mDrawPathHeight = (mMaxLat - mMinLat) * mHeightLatRatio;
//
//        mXOffset = (mWidth - mDrawPathWidth) / 2;
//        mYOffset = (mHeight - mDrawPathHeight) / 2;
//
//        LogUtils.sysPrint(TAG, "change ratio result. maxLng:" +
//                mMaxLng + ", minLng:" + mMinLng + ", maxLat:" + mMaxLat + ", minLat:" + mMinLat +
//                ". draw area:" + mDrawAreaWidth + "," + mDrawAreaHeight +
//                ". path distance:" + mPathLngDistance + "," + mPathLatDistance +
//                ". ratio:" + mRatio);
//
//    }
//
//    /**
//     * 更新实时轨迹到bitmap上去
//     * 更新实时轨迹到bitmap上去
//     * @param bitmap 待更新的bitmap
//     * @param needRedraw 如果为真告诉pointprovider要求重绘所有轨迹
//     * @return 有更新返回真，无更新返回假
//     */
//    @Override
//    public void drawOnBitmap(Bitmap bitmap) {
//        if (bitmap == null || bitmap.isRecycled()) {
//            LogUtils.sysPrint( TAG, "bitmap is null or has been recycled while draw");
//            return;
//        }
//        LogUtils.sysPrint( TAG, "draw on bitmap");
//        Canvas canvas = new Canvas(bitmap);
//        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
//        synchronized (this) {
//            if (mTmpTrailBitmap == null || mTmpTrailBitmap.isRecycled()) {
//                LogUtils.sysPrint( TAG, "tmp trail bitmap is null or has been recycled while draw");
//            } else {
//                canvas.drawBitmap(mTmpTrailBitmap, 0, 0, mSportTrailPaint);
//            }
//        }
//        LogUtils.sysPrint( TAG, "draw on bitmap finished");
//    }
//
//    @Override
//    public boolean refreshBitmap(boolean needRedraw) {
//        LogUtils.sysPrint( TAG, "refresh bitmap");
//        long startTime = SystemClock.elapsedRealtime();
//        if (mPointProvider ==  null) {
//            LogUtils.sysPrint( TAG, "point provider is null");
//            return false;
//        }
//        if (!isValid()) {
//            LogUtils.sysPrint( TAG, "is not valid while refresh bitmap");
//            return false;
//        }
//        if (mFixedPointBitmap == null || mFixedPointBitmap.isRecycled()) {
//            LogUtils.sysPrint( TAG, "fixed point bitmap is null while refresh bitmap");
//            return false;
//        }
//        if (mTmpTrailBitmap == null || mTmpTrailBitmap.isRecycled()) {
//            LogUtils.sysPrint( TAG, "tmp trail bitmap is null while refresh bitmap");
//            return false;
//        }
//        if (mAllPointListBitmap == null || mAllPointListBitmap.isRecycled()) {
//            LogUtils.sysPrint( TAG, "all point bitmap is null while refresh bitmap");
//            return false;
//        }
//        List<SportLocationData> newUpdatedLocationDatas = new LinkedList<>();
//        List<SportLocationData> newFixedLocationDatas = new LinkedList<>();
//        boolean ret = false;
//        int updateType;
//        synchronized (mPointProvider) {
//            updateType = mPointProvider.getUpdatePoints(
//                    newUpdatedLocationDatas, newFixedLocationDatas, needRedraw);
//            LogUtils.sysPrint( TAG, "update type : " + updateType);
//            switch (updateType) {
//                case IPointProvider.UPDATE_POINT_TYPE_ADD:
//                    updatePointTypeAdd(newUpdatedLocationDatas, newFixedLocationDatas);
//                    ret = true;
//                    break;
//                case IPointProvider.UPDATE_POINT_TYPE_CLEAR_AND_REDRAW:
//                    updatePointTypeClearAndRedraw(newUpdatedLocationDatas, newFixedLocationDatas);
//                    ret = true;
//                    break;
//                case IPointProvider.UPDATE_POINT_TYPE_UPDATE:
//                    updatePointTypeUpdate(newUpdatedLocationDatas, newFixedLocationDatas);
//                    ret = true;
//                    break;
//                default:
//            }
//        }
//        if (mAllPointListBitmap == null || mAllPointListBitmap.isRecycled()) {
//            ret = false;
//        }
//        if (ret) {
//            synchronized (this) {
//                if (mTmpTrailBitmap != null) {
//                    mTmpTrailCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
//                    mTmpTrailCanvas.drawBitmap(mAllPointListBitmap, 0, 0, mSportTrailPaint);
//                    drawStartPoint(mTmpTrailCanvas);
//                    drawEndPoint(mTmpTrailCanvas, getAngle());
//                }
//            }
//        }
//        LogUtils.sysPrint( TAG, "refresh bitmap finished. cost : " +
//                (SystemClock.elapsedRealtime() - startTime) + "(mm)");
//        return ret;
//    }
//
//    private void drawStartPoint(Canvas canvas) {
//        if (mStartBitmap == null) {
//            if (Global.DEBUG_LEVEL_1) {
//                LogUtils.sysPrint(TAG, "start bitmap is null");
//            }
//            return;
//        }
//        if (mStartPoint == null || mStartBitmap.isRecycled()) {
//
//            return;
//        }
//        int startWidth = mStartBitmap.getScaledWidth(canvas);
//        int startHeight = mStartBitmap.getScaledHeight(canvas);
//        canvas.drawBitmap(
//                mStartBitmap, mStartPoint.mX - startWidth / 2,
//                mStartPoint.mY - startHeight / 2, mSportTrailPaint);
//    }
//
//    private void drawEndPoint(Canvas canvas, float angle) {
//        if (mEndBitmap == null || mEndBitmap.isRecycled()) {
//
//            return;
//        }
//        if (mEndPoint == null) {
//
//            return;
//        }
//        LogUtils.sysPrint( TAG, "draw angle : " + angle +
//                ". from " + mPenulTimatePoint + " to " + mEndPoint);
//        int endWidth = mEndBitmap.getScaledWidth(canvas);
//        int endHeight = mEndBitmap.getScaledHeight(canvas);
//        mMatrix.reset();
//        mMatrix.postRotate(
//                angle,
//                endWidth / 2,
//                endHeight / 2);
//        mMatrix.postTranslate(
//                mEndPoint.mX - endWidth / 2,
//                mEndPoint.mY - endHeight / 2);
//        canvas.drawBitmap(mEndBitmap, mMatrix, mSportTrailPaint);
//    }
//
//    /**
//     * 获取旋转角度，向上为0度，顺时针方向
//     * @return
//     */
//    private float getAngle() {
//        if (mEndPoint == null || mPenulTimatePoint == null) {
//            return 0;
//        }
//        float startX = mPenulTimatePoint.mX;
//        float startY = mPenulTimatePoint.mY;
//        float endX = mEndPoint.mX;
//        float endY = mEndPoint.mY;
//        float width = endX - startX;
//        float height = startY - endY;
//        float grad = (float) Math.sqrt(width * width + height * height);
//        if (grad == 0) {
//            return 0;
//        }
//        float sin = width / grad;
//        float arcSin = (float) Math.asin(sin);
//        float degrees = (float) Math.toDegrees(arcSin);
//        float angle = 0;
//        if (height > 0) {
//            angle = degrees;
//        } else {
//            angle = 180 - degrees;
//        }
//
//        return angle;
//    }
//
//    private void updatePointTypeAdd(List<SportLocationData> updatedLocationDatas,
//                                    List<SportLocationData> fixedLocationDatas) {
//        LogUtils.sysPrint( TAG, "update point type add. update : " +
//                updatedLocationDatas + ". fix : " + fixedLocationDatas);
//        //比例没有发生改变，只需要将updateLocationDatas绘制到mLastPointListBitmap中，
//        //将fixedLocationDatas绘制到mFixedPointBitmap中
//        List<Point> updatedPoints = convertToPoint(updatedLocationDatas);
//        if (updatedPoints == null || updatedPoints.isEmpty()) {
//
//            return;
//        }
//        Point currentPoint = updatedPoints.get(updatedPoints.size() - 1);
//        Point lastPoint = null;
//        LogUtils.sysPrint( TAG, "update point size " + updatedPoints.size());
//        if (updatedPoints.size() > 1) {
//            lastPoint = updatedPoints.get(updatedPoints.size() - 2);
//        } else {
//            lastPoint = mEndPoint;
//        }
//        updatePenulTimatePoint(lastPoint, currentPoint);
//        drawOnBitmapWithFirstPoint(mLastUpdatedPoint, updatedPoints, mAllPointCanvas, false);
//        List<Point> fixedPoints = convertToPoint(fixedLocationDatas);
//        if (fixedPoints == null || fixedPoints.isEmpty()) {
//
//        } else {
//            drawOnBitmapWithFirstPoint(mLastFixedPoint, fixedPoints, mFixedPointCanvas, false);
//        }
//        updateLastPoints(updatedPoints, fixedPoints);
//    }
//
//    private void updatePenulTimatePoint(Point lastPoint, Point currentPoint) {
//        if (currentPoint == null) {
//            LogUtils.sysPrint( TAG, "update penultimate point failed. current point is null");
//            return;
//        }
//        if (lastPoint != null && lastPoint.mX == currentPoint.mX && lastPoint.mY == currentPoint.mY) {
//            return;
//        }
//        mPenulTimatePoint = lastPoint;
//        mEndPoint = currentPoint;
//    }
//
//    private void updateLastPoints(List<Point> updatedPoints,
//                                  List<Point> fixedPoints) {
//        LogUtils.sysPrint( TAG, "update last point. update : " + updatedPoints +
//                ", fix : " + fixedPoints);
//        if (updatedPoints != null && !updatedPoints.isEmpty()) {
//            mLastUpdatedPoint = updatedPoints.get(updatedPoints.size() - 1);
//        }
//        if (fixedPoints != null && !fixedPoints.isEmpty()) {
//            mLastFixedPoint = fixedPoints.get(fixedPoints.size() - 1);
//        }
//    }
//
//    private void drawOnBitmapWithFirstPoint(
//            Point firstPoint, List<Point> points, Canvas canvas, boolean needClear) {
//        if (points == null || points.isEmpty()) {
//            return;
//        }
//        List<Point> tmpPoints = new LinkedList<>(points);
//        if (firstPoint != null) {
//            tmpPoints.add(0, firstPoint);
//        }
//        drawOnBitmap(tmpPoints, canvas, needClear);
//    }
//
//    private void updatePointTypeClearAndRedraw(List<SportLocationData> updatedLocationDatas,
//                                               List<SportLocationData> fixedLocationDatas) {
//        if (updatedLocationDatas == null || fixedLocationDatas == null) {
//
//            return;
//        }
//        if (mFixedPointBitmap == null || mFixedPointBitmap.isRecycled()) {
//            return;
//        }
//        int updateSize = updatedLocationDatas.size();
//        int fixSize = fixedLocationDatas.size();
//        long currentTime = System.currentTimeMillis();
//
//
//        //比例发生改变，重新绘制所有点，将updateLocationDatas绘制到mLastPointListBitmap中，
//        //将fixedLocationDatas绘制到mFixedPointBitmap中
//        updateBorder();
//
//        //log
//        updateRatio(updatedLocationDatas);
//
//        //log
//        float[][] updatedPoints = /*convertToPoint(updatedLocationDatas);*/convertToArrays(updatedLocationDatas);
//        float[][] fixedPoints = /*convertToPoint(fixedLocationDatas);*/convertToArrays(fixedLocationDatas);
//
//        //log
//        mStartPoint = null;
//        if (fixedPoints != null && fixedPoints.length > 0) {
//            mStartPoint = /*fixedPoints.get(0);*/
//                    new Point((long) fixedPoints[0][0], fixedPoints[0][1], fixedPoints[0][2], (int) fixedPoints[0][3]);
//        } else if (updatedPoints != null && updatedPoints.length > 0) {
//            mStartPoint = /*updatedPoints.get(0);*/
//                    new Point((long) updatedPoints[0][0], updatedPoints[0][1], updatedPoints[0][2], (int) updatedPoints[0][3]);
//        } else {
//
//            return;
//        }
//
//
//        //log
//        if (updatedPoints != null && updatedPoints.length > 0) {
//            Point currentPoint = /*updatedPoints.get(updatedPoints.size() - 1);*/
//                    new Point((long) updatedPoints[updatedPoints.length - 1][0], updatedPoints[updatedPoints.length - 1][1],
//                            updatedPoints[updatedPoints.length - 1][2], (int) updatedPoints[updatedPoints.length - 1][3]);
//            Point lastPoint = null;
//            if (updatedPoints.length > 1) {
//                lastPoint = /*updatedPoints.get(updatedPoints.size() - 2);*/
//                        new Point((long) updatedPoints[updatedPoints.length - 2][0], updatedPoints[updatedPoints.length - 2][1],
//                                updatedPoints[updatedPoints.length - 2][2], (int) updatedPoints[updatedPoints.length - 2][3]);
//            }
//            updatePenulTimatePoint(lastPoint, currentPoint);
//        }
//
//        //log
//        drawOnBitmap(fixedPoints, mFixedPointCanvas);
//        mAllPointCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
//
//        //log
//        mAllPointCanvas.drawBitmap(mFixedPointBitmap, 0, 0, mSportTrailPaint);
//
//        //log
//        updateLastPoints(updatedPoints, fixedPoints);
//
//        if (mLastFixedPoint == null) {
//            drawOnBitmap(updatedPoints, mAllPointCanvas);
//        } else {
//            drawOnBitmapWithFirstPoint(mLastFixedPoint, updatedPoints, mAllPointCanvas);
//        }
//
//    }
//
//    /**
//     * fix sport crash bugs when add new point to a list start
//     * @Author machenglin 2017/02/17
//     * */
//    private float[][] convertToArrays(List<? extends SportLocationData> gpsDatas) {
//        if (Global.DEBUG_VIEW) {
//            LogUtils.sysPrint(TAG, "convert to point|points:" + gpsDatas + "|distance/lng:" +
//                    mDistancePerLng + "|distance/lat:" + mDistancePerLat + "|mRatio:" + mRatio);
//        }
//        if (gpsDatas == null || gpsDatas.isEmpty()) {
//            return null;
//        }
//
//        float[][] result = new float[gpsDatas.size()][4];
//
//        for (int i = 0; i < gpsDatas.size(); i++) {
//            float x = (gpsDatas.get(i).mLongitude - mMinLng) * mWidthLngRatio + mXOffset;
//            float y = (mMaxLat - gpsDatas.get(i).mLatitude) * mHeightLatRatio + mYOffset;
//            result[i][0] = gpsDatas.get(i).mTimestamp;
//            result[i][1] = x;
//            result[i][2] = y;
//            result[i][3] = DEFAULT_LINE_COLOR;
//        }
//        return result;
//    }
//
//    private void updateLastPoints(Point lastUpdatePoint,
//                                  Point LastFixedPoint) {
//
//        if (lastUpdatePoint != null) {
//            mLastUpdatedPoint = lastUpdatePoint;
//        }
//        if (LastFixedPoint != null) {
//            mLastFixedPoint = LastFixedPoint;
//        }
//    }
//
//    private void updateLastPoints(float[][] updatePoints,
//                                  float[][] fixedPoints) {
//        if (updatePoints != null && updatePoints.length > 0) {
//            mLastUpdatedPoint = new Point((long)updatePoints[updatePoints.length -1][0], updatePoints[updatePoints.length -1][1],
//                    updatePoints[updatePoints.length -1][2], (int) updatePoints[updatePoints.length -1][3]);
//        }
//        if (fixedPoints != null && fixedPoints.length > 0) {
//            mLastFixedPoint = new Point((long)fixedPoints[fixedPoints.length -1][0], fixedPoints[fixedPoints.length -1][1],
//                    fixedPoints[fixedPoints.length -1][2], (int) fixedPoints[fixedPoints.length -1][3]);
//        }
//    }
//
//    private void drawOnBitmapWithFirstPoint(
//            Point firstPoint, float[][] points, Canvas canvas) {
//        if (points == null || points.length < 1) {
//            return;
//        }
//
//
//        if (mDrawAreaWidth <= 0 || mDrawAreaHeight <= 0) {
//            return;
//        }
//        //绘制轨迹
//        boolean isFirst = true;
//
//        float lastX = 0;
//        float lastY = 0;
//        int lastColor = 0;
//        if (firstPoint != null) {
//            if (isFirst) {
//                isFirst = false;
//            }
//            lastX = firstPoint.mX;
//            lastY = firstPoint.mY;
//            lastColor = firstPoint.mColor;
//        }
//        for (int i = 0; i < points.length; i++) {
//            if (isFirst) {
//                isFirst = false;
//            } else {
//                mShader = new LinearGradient(lastX, lastY, points[i][1], points[i][2], lastColor, (int)points[i][3], Shader.TileMode.CLAMP);
//                mSportTrailPaint.setShader(mShader);
//                canvas.drawLine(lastX, lastY, points[i][1],  points[i][2], mSportTrailPaint);
//            }
//            lastX = points[i][1];
//            lastY = points[i][2];
//            lastColor = (int)points[i][3];
//        }
//    }
//
//    private void drawOnBitmap(float[][] points, Canvas canvas) {
//
//        if (points == null || points.length < 1) {
//            return;
//        }
//        if (mDrawAreaWidth <= 0 || mDrawAreaHeight <= 0) {
//            return;
//        }
//        //绘制轨迹
//        boolean isFirst = true;
//
//        float lastX = 0;
//        float lastY = 0;
//        int lastColor = 0;
//        for (int i = 0; i < points.length; i++) {
//            if (isFirst) {
//                isFirst = false;
//            } else {
//                mShader = new LinearGradient(lastX, lastY, points[i][1], points[i][2], lastColor, (int)points[i][3], Shader.TileMode.CLAMP);
//                mSportTrailPaint.setShader(mShader);
//                canvas.drawLine(lastX, lastY, points[i][1],  points[i][2], mSportTrailPaint);
//            }
//            lastX = points[i][1];
//            lastY = points[i][2];
//            lastColor = (int)points[i][3];
//        }
//    }
//    /**
//     * fix sport crash bugs when add new point to a list end
//     * @Author machenglin 2017/02/17
//     * */
//    private void updatePointTypeUpdate(List<SportLocationData> updatedLocationDatas,
//                                       List<SportLocationData> fixedLocationDatas) {
//        if (updatedLocationDatas == null) {
//
//            return;
//        }
//        if (mStartPoint == null || mEndPoint == null) {
//
//            return;
//        }
//        if (mFixedPointBitmap == null || mFixedPointBitmap.isRecycled()) {
//
//            return;
//        }
//        //比例没有发生改变，只需要更新updateLocationDatas绘制到mLastPointListBitmap中
//        //mFixedPointBitmap不需要改变
//        List<Point> updatedPoints = convertToPoint(updatedLocationDatas);
//        if (updatedPoints == null || updatedPoints.isEmpty()) {
//            return;
//        }
//        Point firstPoint = updatedPoints.get(0);
//        Point endPoint = updatedPoints.get(updatedPoints.size() - 1);
//        if (mStartPoint.mId == firstPoint.mId) {
//            mStartPoint = firstPoint;
//        }
//        Point currentPoint = endPoint;
//        Point lastPoint = null;
//        if (updatedPoints.size() > 1) {
//            lastPoint = updatedPoints.get(updatedPoints.size() - 2);
//        }
//        updatePenulTimatePoint(lastPoint, currentPoint);
//        mAllPointCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
//        mAllPointCanvas.drawBitmap(mFixedPointBitmap, 0, 0, mSportTrailPaint);
//        drawOnBitmapWithFirstPoint(mLastFixedPoint, updatedPoints, mAllPointCanvas, false);
//        updateLastPoints(updatedPoints, null);
//    }
//
//    private List<Point> convertToPoint(List<? extends SportLocationData> gpsDatas) {
//
//        if (gpsDatas == null || gpsDatas.isEmpty()) {
//            return null;
//        }
//
//        List<Point> result = new LinkedList<>();
//
//        for (SportLocationData gpsData : gpsDatas) {
//            float x = (gpsData.mLongitude - mMinLng) * mWidthLngRatio + mXOffset;
//            float y = (mMaxLat - gpsData.mLatitude) * mHeightLatRatio + mYOffset;
//            result.add(new Point(gpsData.mTimestamp, x, y, DEFAULT_LINE_COLOR));
//        }
//        return result;
//    }
//
//    private void drawOnBitmap(List<Point> points, Canvas canvas, boolean needClear) {
//
//        if (points == null || points.isEmpty()) {
//            return;
//        }
//        if (mDrawAreaWidth <= 0 || mDrawAreaHeight <= 0) {
//            return;
//        }
//        //绘制轨迹
//        if (needClear) {
//            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
//        }
//        boolean isFirst = true;
//
//        float lastX = 0;
//        float lastY = 0;
//        int lastColor = 0;
//        for (int i = 0; i < points.size(); i++) {
//            Point p = points.get(i);
//            if (isFirst) {
//                isFirst = false;
//            } else {
//                mShader = new LinearGradient(lastX, lastY, p.mX, p.mY, lastColor, p.mColor, Shader.TileMode.CLAMP);
//                mSportTrailPaint.setShader(mShader);
//                canvas.drawLine(lastX, lastY, p.mX, p.mY, mSportTrailPaint);
//            }
//            lastX = p.mX;
//            lastY = p.mY;
//            lastColor = p.mColor;
//        }
//    }
//
//    public boolean isValid(){
//        return status != STATUS_STOPED;
//    }
//
//    public void setStatus(int s){
//        status = s;
//    }
//
//    @Override
//    public void stop() {
//        setStatus(STATUS_STOPED);
//        synchronized (this) {
//            if (mFixedPointBitmap != null) {
//                mFixedPointBitmap.recycle();
//                mFixedPointBitmap = null;
//            }
//            if (mAllPointListBitmap != null) {
//                mAllPointListBitmap.recycle();
//                mAllPointListBitmap = null;
//            }
//            if (mTmpTrailBitmap != null) {
//                mTmpTrailBitmap.recycle();
//                mTmpTrailBitmap = null;
//            }
//            if (mStartBitmap != null) {
//                mStartBitmap.recycle();
//                mStartBitmap = null;
//            }
//            if (mEndBitmap != null) {
//                mEndBitmap.recycle();
//                mEndBitmap = null;
//            }
//        }
//    }
//}

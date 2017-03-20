package com.huami.watch.gps.view;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PorterDuff;
import android.graphics.Shader.TileMode;
import android.location.Location;

import com.hs.gpxparser.modal.Waypoint;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhangfan on 15-11-27.
 */
public class TrailDrawer {
    private static final float INVALID_LNG_LAT = 1800;
    private static final float HIGH_SPEED_SHRESHOLD = 1;
    private static final int DEFAULT_WIDTH = 196;
    private static final int DEFAULT_HEIGHT = 196;
    private static final int INNER_MARGIN = 30;
    private static final String TAG = "TrailDrawer";
    private static final int FAST_LINE_COLOR = 0xffffffff;
    private static final int SLOW_LINE_COLOR = 0xffffffff;
    private static final int PAUSE_LINE_COLOR = 0xaa000000;
    private static final int DEFAULT_LINE_COLOR = 0xffff0000;
    private static final int DEFAULT_LOW_SPEED_COLOR = 0xff09ff00;
    private static final int DEFAULT_HIGH_SPEED_COLOR = 0xffff0000;
    private static final int LINE_WIDTH = 4;
//    private static final int SPEED_EXPAND_TIME = 1000;
    /**
     *  The max value of meter pixels / meter = 200px / 100m
     */
    private static final float MAX_PIXELS_METER_RATIO = 2f;
    ArgbEvaluator mEvaluator = new ArgbEvaluator();
    private Bitmap mTrailBitmap = null;
    private Bitmap mWholeTrailBitmap = null;
    private List<Waypoint> mLocationDatas = new LinkedList<>();
    private int mWidth = 0;
    private int mHeight = 0;
    private float mMinLng = INVALID_LNG_LAT;
    private float mMaxLng = -INVALID_LNG_LAT;
    private float mMinLat = INVALID_LNG_LAT;
    private float mMaxLat = -INVALID_LNG_LAT;
    private float mDistancePerLat = -1;
    private float mDistancePerLng = -1;
    private int mDrawAreaWidth = DEFAULT_WIDTH - INNER_MARGIN;
    private int mDrawAreaHeight = DEFAULT_HEIGHT - INNER_MARGIN;
    private float mPathLngDistance = -1;
    private float mPathLatDistance = -1;
    private float mRatio = -1;
    private Paint mSportTrailPaint = null;
    private Paint mPausedTrailPaint = null;
    private LinearGradient mShader;
    private Point mFirstPoint = null;
    private Point mPreLastPoint = null;
    private Point mLastPoint = null;
    private float mDrawPathWidth = 0;
    private float mDrawPathHeight = 0;
    private Matrix mMatrix = null;
    private Context mContext = null;
    private Bitmap mStartBitmap = null;
    private Bitmap mEndBitmap = null;
    private Paint mWholeTrailPaint = null;
    private Canvas mWholeTrailCanvas = null;
    private int mDefaultLineColor = DEFAULT_LINE_COLOR;
    private float mXOffset = 0;
    private float mYOffset = 0;
    private float mWidthLngRatio = 0;
    private float mHeightLatRatio = 0;
    private boolean mColorfulPath = false;
    private int mMaxSpeed = -1;
    private int mMinSpeed = Integer.MAX_VALUE;
    private float mSpeedSpan = 0;
    private Bitmap.Config mBitmapConfig = Bitmap.Config.ARGB_8888;

    public TrailDrawer(Context context, int bitmapWidth, int bitmapHeight) {
        this(context, bitmapWidth, bitmapHeight, false);
    }

    public TrailDrawer(Context context,
                       int bitmapWidth,
                       int bitmapHeight,
                       boolean colorfulPath) {
        mWidth = bitmapWidth;
        mHeight = bitmapHeight;
        mDrawAreaWidth = mWidth - INNER_MARGIN;
        mDrawAreaHeight = mHeight - INNER_MARGIN;
        mColorfulPath = colorfulPath;
        init(context);
    }

    private void init(Context context) {
        mContext = context;

        mSportTrailPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mSportTrailPaint.setColor(DEFAULT_LINE_COLOR);
        mSportTrailPaint.setStyle(Paint.Style.STROKE);
        mSportTrailPaint.setStrokeWidth(LINE_WIDTH);
        mSportTrailPaint.setStrokeCap(Cap.ROUND);

        mPausedTrailPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPausedTrailPaint.setPathEffect(new DashPathEffect(new float[]{1, 2, 3, 4}, 1));
        mPausedTrailPaint.setColor(PAUSE_LINE_COLOR);
        mPausedTrailPaint.setStrokeWidth(LINE_WIDTH);

        mWholeTrailPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

        mMatrix = new Matrix();

        mFirstPoint = null;
        mLastPoint = null;
        mPreLastPoint = null;
    }

    private void initBitmapIfNotExist() {
        if (mWidth <= 0 || mHeight <= 0) {
            throw new IllegalArgumentException(
                    "width or height should not less than zero. width:" +
                            mWidth + ", height:" + mHeight);
        }
        synchronized (this) {
            if (mTrailBitmap == null) {
                mTrailBitmap = Bitmap.createBitmap(mWidth, mHeight, mBitmapConfig);
            }
            if (mWholeTrailBitmap == null) {
                mWholeTrailBitmap = Bitmap.createBitmap(mWidth, mHeight, mBitmapConfig);
                mWholeTrailCanvas = new Canvas(mWholeTrailBitmap);
            }
        }
    }

    public synchronized void clearState() {

        mMinLng = INVALID_LNG_LAT;
        mMaxLng = -INVALID_LNG_LAT;
        mMinLat = INVALID_LNG_LAT;
        mMaxLat = -INVALID_LNG_LAT;
        mLastPoint = null;
        mPreLastPoint = null;
        mFirstPoint = null;
        mPathLngDistance = -1;
        mPathLatDistance = -1;
        mRatio = -1;
        mLocationDatas.clear();
    }

    public synchronized void addLocationDatasToBitmap(List<? extends Waypoint> locationDatas) {

        initBitmapIfNotExist();
        if (locationDatas == null || locationDatas.isEmpty()) {
            return;
        }
        mLocationDatas.addAll(locationDatas);
        boolean checkResult = checkAndUpdateMaxMinLatLng(locationDatas);
        List<Point> points = null;
        //需要重新刷新比例
        initDraw(mLocationDatas);
        if (checkResult) {

            points = convertToPoint(mLocationDatas);

            if (points == null) {
                throw new IllegalStateException("points should not be null");
            }
            mFirstPoint = points.get(0);
            if (points.size() <= 1) {
                mPreLastPoint = mLastPoint;
            } else {
                mPreLastPoint = points.get(points.size() - 2);
            }
            mLastPoint = points.get(points.size() - 1);

        } else {

            //比例不需要改变，还在之前的基础上画
            points = convertToPoint(locationDatas);

            if (points == null || points.isEmpty()) {
                return;
            }
            points.add(0, mLastPoint);//mLastPoint一定不会为null，因为checkResult为真
            if (points.size() <= 1) {
                mPreLastPoint = mLastPoint;
            } else {
                mPreLastPoint = points.get(points.size() - 2);
            }
            mLastPoint = points.get(points.size() - 1);
        }
        synchronized (this) {
            mPoints = points;
            if (mTrailBitmap != null) {
                drawOnBitmap(points, mTrailBitmap, checkResult);
            }
        }
    }

    private List<Point> mPoints;

    public Point[] getDrawPoints() {
        if (mPoints != null && !mPoints.isEmpty()) {
            Point[] result = new Point[mPoints.size()];
            mPoints.toArray(result);
            return result;
        } else {
            return null;
        }
    }

    private void drawOnBitmap(List<Point> points, Bitmap bitmap, boolean needClear) {

        if (mDrawAreaWidth <= 0 || mDrawAreaHeight <= 0) {
            return;
        }
        //绘制轨迹
        Canvas canvas = new Canvas(bitmap);
        if (needClear) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }
        boolean isFirst = true;

        float lastX = 0;
        float lastY = 0;
        int lastColor = 0;
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            if (isFirst) {
                isFirst = false;
            } else {
                mShader = new LinearGradient(lastX, lastY, p.mX, p.mY, lastColor, p.mColor, TileMode.CLAMP);
                mSportTrailPaint.setShader(mShader);
                canvas.drawLine(lastX, lastY, p.mX, p.mY, mSportTrailPaint);
            }
            lastX = p.mX;
            lastY = p.mY;
            lastColor = p.mColor;
        }
    }

    public Point getFirstPoint() {
        return mFirstPoint;
    }

    public Point getPreLastPoint() {
        return mPreLastPoint;
    }

    public Point getLastPoint() {
        return mLastPoint;
    }

    private float getAngle(float startX, float startY, float endX, float endY) {
        float width = endX - startX;
        float height = startY - endY;
        float grad = (float) Math.sqrt(width * width + height * height);
        float sin = height / grad;
        float arcSin = (float) Math.asin(sin);
        float degrees = (float) Math.toDegrees(arcSin);
        float angle = 0;
        if (width > 0) {
            angle = degrees;
        } else {
            angle = 180 - degrees;
        }

        return angle;
    }

    public void startDraw() {
        clearState();
    }

    public synchronized void finishDraw() {
        if (mTrailBitmap != null) {
            mTrailBitmap.recycle();
            mTrailBitmap = null;
        }
        if (mWholeTrailBitmap != null) {
            mWholeTrailBitmap.recycle();
            mWholeTrailBitmap = null;
            mWholeTrailCanvas = null;
        }
        if (mStartBitmap != null) {
            mStartBitmap.recycle();
            mStartBitmap = null;
        }
        if (mEndBitmap != null) {
            mEndBitmap.recycle();
            mEndBitmap = null;
        }
        clearState();
    }

    public void setStart(int startRes) {
        mStartBitmap = BitmapFactory.decodeResource(mContext.getResources(), startRes);
    }

    public void setEnd(int endRes) {
        mEndBitmap = BitmapFactory.decodeResource(mContext.getResources(), endRes);
    }

    public synchronized Bitmap newTrailBitmap() {
        initBitmapIfNotExist();
        mWholeTrailCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        mWholeTrailCanvas.drawBitmap(mTrailBitmap, 0, 0, mWholeTrailPaint);
        TrailDrawer.Point firstPoint = getFirstPoint();
        TrailDrawer.Point lastPoint = getLastPoint();
        TrailDrawer.Point preLastPoint = getPreLastPoint();
        if (firstPoint == null || firstPoint == lastPoint ||
                mTrailBitmap == null || Float.isInfinite(mRatio)) {
            if (mEndBitmap != null) {
                mWholeTrailCanvas.drawBitmap(
                        mEndBitmap,
                        (mWidth - mEndBitmap.getScaledWidth(mWholeTrailCanvas)) / 2,
                        (mHeight - mEndBitmap.getScaledHeight(mWholeTrailCanvas)) / 2,
                        mWholeTrailPaint);
            }
            return mWholeTrailBitmap;
        }
        if (mStartBitmap != null) {
            float firstX = firstPoint.mX;
            float firstY = firstPoint.mY;

            int startWidth = mStartBitmap.getScaledWidth(mWholeTrailCanvas);
            int startHeight = mStartBitmap.getScaledHeight(mWholeTrailCanvas);
            mWholeTrailCanvas.drawBitmap(
                    mStartBitmap,
                    firstX - startWidth / 2,
                    firstY - startHeight / 2,
                    mWholeTrailPaint);
        }

        if (mEndBitmap != null) {
            float lastX = preLastPoint.mX;
            float lastY = preLastPoint.mY;
            float endX = lastPoint.mX;
            float endY = lastPoint.mY;
            float angle = 90 - getAngle(lastX, lastY, endX, endY);
            mMatrix.reset();
            /*mMatrix.postRotate(
                    angle,
                    mEndBitmap.getScaledWidth(mWholeTrailCanvas) / 2,
                    mEndBitmap.getScaledHeight(mWholeTrailCanvas) / 2);*/
            mMatrix.postTranslate(
                    endX - mEndBitmap.getScaledWidth(mWholeTrailCanvas) / 2,
                    endY - mEndBitmap.getScaledHeight(mWholeTrailCanvas) / 2);
            mWholeTrailCanvas.drawBitmap(mEndBitmap, mMatrix, mWholeTrailPaint);
        }
        return mWholeTrailBitmap;
    }

    private void initDistancePerLatLng(float lat, float lng) {
        if (mDistancePerLat < 0 || mDistancePerLng < 0) {
            float[] w = new float[1];
            Location.distanceBetween(lat, lng, lat + 1, lng, w);
            mDistancePerLat = w[0];
            Location.distanceBetween(lat, lng, lat, lng + 1, w);
            mDistancePerLng = w[0];
        }
    }

    private void initDraw(List<Waypoint> latLngs) {
        if (latLngs == null || latLngs.isEmpty()) {
            return;
        }
        if (mWidth <= 0 || mHeight <= 0) {
            return;
        }

        initDistancePerLatLng((float)latLngs.get(0).getLatitude(), (float) latLngs.get(0).getLongitude());

        mPathLngDistance = mDistancePerLng * (mMaxLng - mMinLng);
        mPathLatDistance = mDistancePerLat * (mMaxLat - mMinLat);

        if (Math.max(mPathLngDistance, mPathLatDistance) < Math.min(mDrawAreaWidth, mDrawAreaHeight) / MAX_PIXELS_METER_RATIO) {
            mRatio = MAX_PIXELS_METER_RATIO;
        } else if (mPathLatDistance != 0 && mPathLngDistance == 0) {
            mRatio = (float) mDrawAreaHeight / mPathLatDistance;
        } else if (mPathLatDistance == 0 && mPathLngDistance != 0) {
            mRatio = (float) mDrawAreaWidth / mPathLngDistance;
        } else {
            float actualRatio = mPathLngDistance / mPathLatDistance;
            float viewRatio = (float) mDrawAreaWidth / mDrawAreaHeight;
            if (actualRatio > viewRatio) {
                mRatio = (float) mDrawAreaWidth / mPathLngDistance;
            } else {
                mRatio = (float) mDrawAreaHeight / mPathLatDistance;
            }
        }

        mWidthLngRatio = mDistancePerLng * mRatio;
        mHeightLatRatio = mDistancePerLat * mRatio;

        mDrawPathWidth = (mMaxLng - mMinLng) * mWidthLngRatio;
        mDrawPathHeight = (mMaxLat - mMinLat) * mHeightLatRatio;

        mXOffset = (mWidth - mDrawPathWidth) / 2;
        mYOffset = (mHeight - mDrawPathHeight) / 2;
    }

    private List<Point> convertToPoint(List<? extends Waypoint> gpsPoint) {

        if (gpsPoint == null || gpsPoint.isEmpty()) {
            return null;
        }

        List<Point> result = new LinkedList<>();

        for (Waypoint point : gpsPoint) {
            float x =(float) (point.getLongitude() - mMinLng) * mWidthLngRatio + mXOffset;
            float y = (float)(mMaxLat - point.getLatitude()) * mHeightLatRatio + mYOffset;
            if (mColorfulPath) {
                int color = DEFAULT_LOW_SPEED_COLOR;
//                if (mSpeedSpan != 0) {
//                    color = (int) mEvaluator.evaluate(
//                            (point.mSpeed - mMinSpeed) / mSpeedSpan,
//                            DEFAULT_LOW_SPEED_COLOR,
//                            DEFAULT_HIGH_SPEED_COLOR);
//                }
                result.add(new Point(x, y, color));
            } else {
                result.add(new Point(x, y, DEFAULT_LINE_COLOR));
            }
        }
        return result;
    }

    private boolean checkAndUpdateMaxMinLatLng(List<? extends Waypoint> locationDatas) {

        boolean result = false;
        for (Waypoint locationData : locationDatas) {

            if (mMinLat > locationData.getLatitude()) {
                mMinLat = (float) locationData.getLatitude();
                result = true;
            }
            if (mMinLng > locationData.getLongitude()) {
                mMinLng = (float) locationData.getLongitude();
                result = true;
            }
            if (mMaxLat < locationData.getLatitude()) {
                mMaxLat = (float) locationData.getLatitude();
                result = true;
            }
            if (mMaxLng < locationData.getLongitude()) {
                mMaxLng = (float) locationData.getLongitude();
                result = true;
            }
//            if (mMinSpeed > locationData.mSpeed) {
//                mMinSpeed = (int) (SportDataFilterUtils.parseSpeed(locationData.mSpeed));
//            }
//            if (mMaxSpeed < locationData.mSpeed) {
//                mMaxSpeed = (int) (SportDataFilterUtils.parseSpeed(locationData.mSpeed));
//            }
        }
        mSpeedSpan = mMaxSpeed - mMinSpeed;

        return result;
    }

    public static final class Point {
        public final float mX;
        public final float mY;
        public final int mColor;

        public Point(float x, float y, int color) {
            mX = x;
            mY = y;
            mColor = color;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "mX=" + mX +
                    ", mY=" + mY +
                    ", mColor=" + mColor +
                    '}';
        }
    }
}

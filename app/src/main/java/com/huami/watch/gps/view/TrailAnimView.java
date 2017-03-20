//package com.huami.watch.gps.view;
//
//
//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;
//import android.animation.AnimatorSet;
//import android.animation.ValueAnimator;
//import android.animation.ValueAnimator.AnimatorUpdateListener;
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.LinearGradient;
//import android.graphics.Paint;
//import android.graphics.Paint.Cap;
//import android.graphics.Paint.Style;
//import android.graphics.Shader.TileMode;
//import android.graphics.drawable.Drawable;
//import android.util.AttributeSet;
//import android.view.View;
//import android.view.animation.LinearInterpolator;
//
//import com.hs.gpxparser.modal.Waypoint;
//import com.huami.watch.gps.utils.BackInterpolator;
//
///**
// * 自定义 animation 轨迹动画
// *
// * 数据点 wayPoint
// */
//public class TrailAnimView extends View {
//
//    public TrailAnimView(Context context) {
//        this(context, null);
//    }
//
//    public TrailAnimView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public TrailAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
//        this(context, attrs, defStyleAttr, 0);
//    }
//
//    public TrailAnimView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
//        mPaint.setStyle(Style.STROKE);
//        mPaint.setStrokeWidth(LINE_WIDTH);
//        mPaint.setStrokeCap(Cap.ROUND);
//    }
//
//    private static final int LINE_WIDTH = 4;
//    private Drawable mStartBitmap = null;
//    private Drawable mEndBitmap = null;
//    private float mHalfSx, mHalfSy, mHalfEx, mHalfEy;
//    private boolean init = false;
//    private boolean mDraw = false, mDrawEnd;
//    private Waypoint[] mPoints;
//    private Paint mPaint;
//    private Waypoint mFirstP, mLastP;
//    private LinearGradient mShader;
//
//    private float step = 1f;
//    private float drawFraction = 0f;
//    private float tmpX, tmpY;
//    private int tmpColor;
//
//    private AnimatorSet mAnims;
//    private float mScale = 0f;
//
//    public void setStart(int startRes) {
//        mStartBitmap = getResources().getDrawable(startRes);
//        if (mStartBitmap != null) {
//            mHalfSx = mStartBitmap.getIntrinsicWidth() * .5f;
//            mHalfSy = mStartBitmap.getIntrinsicHeight() * .5f;
//            mStartBitmap.setBounds(0, 0, mStartBitmap.getIntrinsicWidth(), mStartBitmap.getIntrinsicHeight());
//        }
//    }
//
//    public void setEnd(int endRes) {
//        mEndBitmap = getResources().getDrawable(endRes);
//        if (mEndBitmap != null) {
//            mHalfEx = mEndBitmap.getIntrinsicWidth() * .5f;
//            mHalfEy = mEndBitmap.getIntrinsicHeight() * .5f;
//            mEndBitmap.setBounds(0, 0, mEndBitmap.getIntrinsicWidth(), mEndBitmap.getIntrinsicHeight());
//        }
//    }
//
//    public void setPoints(Waypoint[] points) {
//        mPoints = points;
//
//        if (mPoints != null && mPoints.length > 0) {
//            mFirstP = mPoints[0];
//            mLastP = mPoints[mPoints.length - 1];
//            step = 1f / (mPoints.length - 1);
//            init = true;
//        }
//
//        mDraw = false;
//        mDrawEnd = false;
//    }
//
//    public void startTrailAnim() {
//        mDrawEnd = false;
//        mAnims = new AnimatorSet();
//
//        if (init) {
//            ValueAnimator mTrailAnim = ValueAnimator.ofFloat(0f, 1f);
//            mTrailAnim.setDuration(650);
//            mTrailAnim.setInterpolator(new LinearInterpolator());
//            mTrailAnim.addUpdateListener(new AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    drawFraction = (Float) animation.getAnimatedValue();
//                    invalidate();
//                }
//            });
//            mAnims.play(mTrailAnim);
//
//            ValueAnimator mAlphaAnim = ValueAnimator.ofInt(0, 255);
//            mAlphaAnim.setDuration(80);
//            mAlphaAnim.setStartDelay(620);
//            mAlphaAnim.addUpdateListener(new AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    int alpha = (Integer) animation.getAnimatedValue();
//                    mEndBitmap.setAlpha(alpha);
//                }
//            });
//            mAnims.play(mAlphaAnim);
//
//            ValueAnimator mScaleAnim = ValueAnimator.ofFloat(0f, 1f);
//            mScaleAnim.setDuration(250);
//            mScaleAnim.setStartDelay(620);
//            mScaleAnim.setInterpolator(new BackInterpolator(BackInterpolator.OUT));
//            mScaleAnim.addUpdateListener(new AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    mScale = (Float) animation.getAnimatedValue();
//                    invalidate();
//                }
//            });
//            mScaleAnim.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//                    mDrawEnd = true;
//                }
//            });
//            mAnims.play(mScaleAnim);
//        } else {
//            ValueAnimator mScaleAnim = ValueAnimator.ofFloat(0f, 1f);
//            mScaleAnim.setDuration(250);
//            mScaleAnim.setInterpolator(new BackInterpolator(BackInterpolator.OUT));
//            mScaleAnim.addUpdateListener(new AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    mScale = (Float) animation.getAnimatedValue();
//                    invalidate();
//                }
//            });
//            mAnims.play(mScaleAnim);
//        }
//
//        mAnims.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                setLayerType(View.LAYER_TYPE_NONE, null);
//            }
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                setLayerType(View.LAYER_TYPE_HARDWARE, null);
//            }
//        });
//
//        mAnims.start();
//        mDraw = true;
//    }
//
//    public void cancelTrailAnim() {
//        if (mAnims != null && mAnims.isStarted()) {
//            mAnims.cancel();
//        }
//        mAnims = null;
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        if (mDraw) {
//            if (init) {
//                Waypoint last = mPoints[0];
//                Waypoint curr;
//                float indexFraction = drawFraction / step;
//                int endIndex = (int) indexFraction;
//                for (int i = 1; i <= endIndex; i++) {
//                    curr = mPoints[i];
//                    mShader = new LinearGradient(last.mX, last.mY, curr.mX, curr.mY, last.mColor, curr.mColor,
//                            TileMode.CLAMP);
//                    mPaint.setShader(mShader);
//                    canvas.drawLine(last.mX, last.mY, curr.mX, curr.mY, mPaint);
//                    last = curr;
//                }
//
//                if (endIndex < mPoints.length - 1) {
//                    float crrentFraction = indexFraction - endIndex;
//                    curr = mPoints[endIndex + 1];
//                    evaluatePoint(crrentFraction, last, curr);
//                    mShader = new LinearGradient(last.mX, last.mY, tmpX, tmpY, last.mColor, tmpColor, TileMode.CLAMP);
//                    mPaint.setShader(mShader);
//                    canvas.drawLine(last.mX, last.mY, tmpX, tmpY, mPaint);
//                }
//
//                if (mStartBitmap != null) {
//                    canvas.save(Canvas.MATRIX_SAVE_FLAG);
//                    canvas.translate(mFirstP.mX - mHalfSx, mFirstP.mY - mHalfSy);
//                    mStartBitmap.draw(canvas);
//                    canvas.restore();
//                }
//
//                if (mDrawEnd && mEndBitmap != null) {
//                    canvas.save(Canvas.MATRIX_SAVE_FLAG);
//                    canvas.translate(mLastP.mX, mLastP.mY);
//                    canvas.scale(mScale, mScale);
//                    canvas.translate(-mHalfEx, -mHalfEy);
//                    mEndBitmap.draw(canvas);
//                    canvas.restore();
//                }
//            } else {
//                if (mStartBitmap != null) {
//                    canvas.save(Canvas.MATRIX_SAVE_FLAG);
//                    canvas.translate(getWidth() * .5f, getHeight() * .5f);
//                    canvas.scale(mScale, mScale);
//                    canvas.translate(-mHalfSx, -mHalfSy);
//                    mStartBitmap.draw(canvas);
//                    canvas.restore();
//                }
//            }
//        }
//    }
//
//    private static final int evaluateColor(float fraction, int startColor, int endColor) {
//        int startA = (startColor >> 24) & 0xff;
//        int startR = (startColor >> 16) & 0xff;
//        int startG = (startColor >> 8) & 0xff;
//        int startB = startColor & 0xff;
//
//        int endA = (endColor >> 24) & 0xff;
//        int endR = (endColor >> 16) & 0xff;
//        int endG = (endColor >> 8) & 0xff;
//        int endB = endColor & 0xff;
//
//        return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
//                | (int) ((startR + (int) (fraction * (endR - startR))) << 16)
//                | (int) ((startG + (int) (fraction * (endG - startG))) << 8)
//                | (int) ((startB + (int) (fraction * (endB - startB))));
//    }
//
//    /**
//     * @param fraction
//     * @param startPoint
//     * @param endPoint
//     */
//    private final void evaluatePoint(float fraction, Point startPoint, Point endPoint) {
//        tmpX = startPoint.mX + (endPoint.mX - startPoint.mX) * fraction;
//        tmpY = startPoint.mY + (endPoint.mY - startPoint.mY) * fraction;
//        tmpColor = evaluateColor(fraction, startPoint.mColor, endPoint.mColor);
//    }
//
//}

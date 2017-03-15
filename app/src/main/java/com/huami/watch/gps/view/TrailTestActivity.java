//package com.huami.watch.gps.view;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageView;
//
//public class TrailTestActivity extends Activity {
//
//    private Bitmap mTrailBitmap = null;
//    private AsyncDatabaseManager<SportLocationData> mDatabaseManager = null;
//    private List<SportLocationData> mLocationDatas = null;
//    private int mIndex = 0;
//    private long mTestTrackId = 1463389356000l;
//    private IPointProvider mPointProvider = null;
//    private Bitmap mStartBitmap = null;
//    private Bitmap mEndBitmap = null;
//    private ImageView mImageView = null;
//    private ITrailBitmapProvider mTrailBitmapProvider = null;
//    private boolean mIsShowToast = false;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_trail_test);
//
//
//        mImageView = (ImageView) findViewById(R.id.iv);
//
//        mPointProvider = PointProviderManager.getInstance().getPointProvider();
//        mPointProvider.reset();
//
//
//        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                mPointProvider.reset();
//                mIndex = 0;
//                mTrailBitmapProvider.refreshBitmap(true);
//                mTrailBitmapProvider.drawOnBitmap(mTrailBitmap);
//                return false;
//            }
//        });
//        mImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mIsShowToast) {
//                    mIsShowToast = false;
//                } else {
//                    mIsShowToast = true;
//                }
//            }
//        });
//
//
//
//        mStartBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sport_start_route);
//        mEndBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sport_running_end_route);
//
//
//        int widthPixel = (int) getResources().getDisplayMetrics().density * 100;
//        int heightPixel = (int) getResources().getDisplayMetrics().density * 100;
//
//
//        mTrailBitmapProvider =
//                new TrailBitmapProviderImpl(
//                        widthPixel,
//                        heightPixel,
//                        mStartBitmap, mEndBitmap, false);
//
//
//        mTrailBitmapProvider.start(mPointProvider);
//
//
//
//
//
//        mTrailBitmap = Bitmap.createBitmap(
//                widthPixel,
//                heightPixel,
//                Bitmap.Config.ARGB_8888);
//
//
//
//        // mLocationDatas : 经纬度数据点集合
//
////        mDatabaseManager = new AsyncDatabaseManager<>(
////                LocationDataDao.getInstance(this), Global.getGlobalCloudHandler());
////
////        mDatabaseManager.selectAll(
////                this, LocationDataDao.WHERE_CLAUSE_BY_TRACK_ID,
////                new String[] { "" + mTestTrackId }, null, null, new Callback() {
////                    @Override
////                    protected void doCallback(int resultCode, Object params) {
////                        mLocationDatas = (List<SportLocationData>) params;
////                    }
////                });
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mTrailBitmap.recycle();
//        mTrailBitmap = null;
//    }
//
//    public void onClick(View view) {
//        if (view.getId() == R.id.read) {
//            read();
//        } else if (view.getId() == R.id.display) {
//            display();
//        }
//    }
//
//    private void read() {
//        if (mLocationDatas == null) {
//            return;
//        }
//        SportLocationData locationData = mLocationDatas.get(mIndex++);
//        mPointProvider.addNewPoint(locationData);
//        if (mIsShowToast) {
//            ToastUtils.showShortToast(this, "read " + locationData.mLongitude + "," + locationData.mLatitude);
//        }
//    }
//
//    private void display() {
//        mTrailBitmapProvider.refreshBitmap(false);
//        mTrailBitmapProvider.drawOnBitmap(mTrailBitmap);
//        mImageView.setImageDrawable(new BitmapDrawable(getResources(), mTrailBitmap));
//    }
//}

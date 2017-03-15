

#### canvas 坐标系的绘制


> 计算经纬度之间的距离  长度


> 长度 转换为 屏幕坐标系点位置


> 适时根据当前点的位置进行坐标轨迹的绘制。


> 数据集样点的取样 绘制。


>



#### 数据点的转换

```

private List<Point> convertToPoint(List<? extends SportLocationData> gpsDatas) {

        if (gpsDatas == null || gpsDatas.isEmpty()) {
            return null;
        }

        List<Point> result = new LinkedList<>();

        for (SportLocationData gpsData : gpsDatas) {
            float x = (gpsData.mLongitude - mMinLng) * mWidthLngRatio + mXOffset;
            float y = (mMaxLat - gpsData.mLatitude) * mHeightLatRatio + mYOffset;
            result.add(new Point(gpsData.mTimestamp, x, y, DEFAULT_LINE_COLOR));
        }
        return result;
    }



```

package com.huami.watch.gps.utils;


import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Created by jinliang on 16/11/11.
 *
 *  子线程与主线程之间的控制工具类
 */

public class RxUtils {

    /**
     * 操作异步
     * @param subscribe
     * @param callBack
     */
    public static<T> Subscription operate(Observable.OnSubscribe<T> subscribe, final IResultCallBack<T> callBack){
        final Subscription subscription =  Observable.create(subscribe).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<T>() {
            @Override
            public void onCompleted() {

            }
            @Override
            public void onError(Throwable e) {
                callBack.onFail(null,e.getLocalizedMessage());
            }

            @Override
            public void onNext(T t) {
                if(callBack==null){
                    return ;
                }
                callBack.onSuccess(t);

            }
        }) ;

        return  subscription;
    }


    /**
     * 延迟任务执行过程
     * @param subscribe
     * @param callBack
     * @param delaySeconds  延迟毫秒数据
     * @param <T>
     * @return
     */
    public static<T> Subscription operateDelayTask(Observable.OnSubscribe<T> subscribe,
                                                   final IResultCallBack<T> callBack,
                                                   int  delaySeconds
                                                   ){
        final Subscription subscription =   Observable.timer(delaySeconds, TimeUnit.SECONDS).create(subscribe).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<T>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        callBack.onFail(null,e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(T t) {
                        if(callBack==null){
                            return ;
                        }
                        callBack.onSuccess(t);

                    }
                }) ;

        return  subscription;


    }

    public static void unsubscribe(Subscription subscription)
    {
        if (subscription != null)
        {
            if (!subscription.isUnsubscribed())
            {
                subscription.unsubscribe();
            } else
            {
                // Already unsubscribed
            }
        } else
        {
            // Subscription doesn't exist
        }
    }

    public static void unsubscribe(Subscription... subscriptions)
    {
        for (Subscription subscription : subscriptions)
        {
            if (subscription != null)
            {
                if (!subscription.isUnsubscribed())
                {
                    subscription.unsubscribe();
                } else
                {
                    // Already unsubscribed
                }
            } else
            {
                // Subscription doesn't exist
            }
        }
    }

}

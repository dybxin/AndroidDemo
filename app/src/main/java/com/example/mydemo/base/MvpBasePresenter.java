package com.example.mydemo.base;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import java.lang.ref.WeakReference;


public class MvpBasePresenter<V extends MvpView> implements MvpPresenter<V>{

    private WeakReference<V> viewRef;

    /**
     * Presenter与View建立连接
     */
    @Override
    public void attachView(V view) {
        viewRef = new WeakReference<>(view);
    }

    /**
     * 每次调用业务请求的时候 即：getView().showXxx();时
     * 请先调用方法检查是否与View建立连接，没有则会空指针异常
     */
    public boolean isViewAttached() {
        return viewRef != null && viewRef.get() != null;
    }

    @UiThread
    @NonNull
    public V getView(){
        return  viewRef == null ? null : viewRef.get();
    }

    /**
     * Presenter与View连接断开
     */
    @Override
    public void detachView() {
        if(viewRef != null){
            viewRef.clear();
            viewRef = null;
        }
    }
}

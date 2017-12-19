package com.example.mydemo.base;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class BaseRxPresenter<V extends MvpView> extends MvpBasePresenter<V> {

    // 管理所有的Disposable, 便于回收资源
    private CompositeDisposable mDisposableComposite;

    @Override
    public void attachView(V view) {
        super.attachView(view);
        mDisposableComposite = new CompositeDisposable();
    }


    @Override
    public void detachView() {
        super.detachView();
        // 与View断开联系时,取消注册Rxjava,防止内存溢出
        if (mDisposableComposite.size()>0){
            mDisposableComposite.dispose();
            mDisposableComposite= null;
        }
    }

    /**
     * 通过该方法添加的Disposable，会在Presenter与View解绑时dispose
     */
    protected void addDisposable(Disposable s) {
        if (mDisposableComposite != null)
            mDisposableComposite.add(s);
    }


}

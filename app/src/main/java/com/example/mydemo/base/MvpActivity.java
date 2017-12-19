package com.example.mydemo.base;

import android.os.Bundle;
import android.support.annotation.NonNull;


public abstract  class MvpActivity<P extends MvpPresenter> extends BaseActivity implements MvpView {

    protected P mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        mPresenter.attachView(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null){
            mPresenter.detachView();
        }
    }

    @NonNull
    public abstract P createPresenter();

    @NonNull
    public P  getPresenter(){
        return mPresenter;
    }


}

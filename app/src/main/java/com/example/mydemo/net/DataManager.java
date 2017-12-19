package com.example.mydemo.net;




import com.example.videostream.model.bean.LoginResult;
import com.example.videostream.model.body.LoginBody;
import com.example.videostream.rx.HttpResultFunc;

import io.reactivex.Observable;


public class DataManager {

    /**
     * 登录
     */
    public static Observable<LoginResult> login(LoginBody body) {
        return RequestClient
                .getServerAPI()
                .login(body)
                .map(new HttpResultFunc<LoginResult>());

    }
}

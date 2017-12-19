package com.example.mydemo.net;






import com.example.mydemo.model.bean.LoginResult;
import com.example.mydemo.model.body.LoginBody;
import com.example.mydemo.rx.HttpResultFunc;

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

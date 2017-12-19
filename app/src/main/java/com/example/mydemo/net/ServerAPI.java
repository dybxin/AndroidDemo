package com.example.mydemo.net;




import com.example.videostream.model.bean.HttpResult;
import com.example.videostream.model.bean.LoginResult;
import com.example.videostream.model.body.LoginBody;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServerAPI {

    /**
     * 登录
     */
    @POST("/signin")
    Observable<HttpResult<LoginResult>> login(@Body LoginBody body);
}

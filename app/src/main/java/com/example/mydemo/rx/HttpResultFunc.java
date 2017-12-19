package com.example.mydemo.rx;



import com.example.videostream.model.bean.HttpResult;

import io.reactivex.functions.Function;

/**
 * 用于map操作符，只想拿HttpResult.data的数据
 * @param <T>   Subscriber真正需要的数据类型，也就是Data部分的数据类型
 */

public class HttpResultFunc<T> implements Function<HttpResult<T>, T> {

    @Override
    public T apply(HttpResult<T> tHttpResult) throws Exception {
        return tHttpResult.data;
    }

}

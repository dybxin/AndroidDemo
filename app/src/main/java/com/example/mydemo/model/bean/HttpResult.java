package com.example.mydemo.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class HttpResult<T> implements Serializable {
    //将访问权限设为public的原因参考http://www.zhihu.com/question/21401198
    @SerializedName("error_code")
    public int code;

    @SerializedName("error_message")
    public String message;

    @SerializedName("response")
    public T data;


    public boolean isSuccessful() {
        return code == 0;
    }
}

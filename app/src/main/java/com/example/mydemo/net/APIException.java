package com.example.mydemo.net;


/**
 * Server API 响应结果含error_code且error_code值不为0时，抛出此异常
 */

public class APIException extends RuntimeException {


    private int code;//错误码


    public APIException(int code , String defaultMessage){
        this(getErrorMessage(code, defaultMessage));
        this.code = code;
    }
    public APIException(int code) {
        this(getErrorMessage(code, null));
        this.code = code;
    }

    public APIException(String message) {
        super(message);
    }


    /**
     * 由于服务器传递过来的错误信息直接给用户看的话，用户未必能够理解
     * 需要根据错误码对错误信息进行一个转换，在显示给用户
     */
    private static String getErrorMessage(int code, String defaultMessage) {
        String message = "";
        switch (code) {
            case -9527:
                message = "网络出错了!";
                break;
            default:
                if (defaultMessage != null) {
                    message = defaultMessage;
                } else {
                    message = "网络出了点问题，请稍后再试 ～";
                }
        }
        return message;
    }

    public int getCode() {
        return code;
    }
}

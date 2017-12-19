package com.example.mydemo.net;



import com.example.mydemo.AppApplication;
import com.example.mydemo.base.Constant;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class RequestClient {

    private static volatile ServerAPI mServerAPI;//单例模式

    public static ServerAPI getServerAPI() {
        if (mServerAPI == null) {
            synchronized (RequestClient.class) {
                if (mServerAPI == null) {
                    mServerAPI = getRetrofitBuilder(getOkHttpClient())
                            .build().create(ServerAPI.class);
                }

            }
        }
        return mServerAPI;
    }

    private static OkHttpClient getOkHttpClient() {

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(Constant.CONNECT_TIME_OUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(Constant.READ_TIME_OUT_SECONDS, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)//重试
                .addInterceptor(new LoggingInterceptor())
                .addInterceptor(new Oauth2Interceptor(AppApplication.context(), Constant.URL_SERVER_API));

        // 自定义一个信任所有证书的TrustManager，添加SSLSocketFactory的时候要用到
        final X509TrustManager trustAllCert =
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                };
        SSLSocketFactory sslSocketFactory = new SSLSocketFactoryCompat(trustAllCert);
        clientBuilder.sslSocketFactory(sslSocketFactory, trustAllCert);

        return clientBuilder.build();
    }

    /**
     * @param client okhttp请求客户端
     * @return retrofit的构建器
     */
    private static Retrofit.Builder getRetrofitBuilder(OkHttpClient client) {
        return new Retrofit.Builder()
                //接口基地址
                .baseUrl(Constant.URL_SERVER_API)
                //Json转换器
                .addConverterFactory(GsonConverterFactory.create())
                //Rxjava转换器：将结果转成已Observable的形式返回
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client);
    }

}

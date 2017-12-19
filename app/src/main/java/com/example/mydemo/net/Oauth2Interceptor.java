package com.example.mydemo.net;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.example.videostream.base.Constant;
import com.example.videostream.utils.DES;
import com.google.api.client.auth.oauth2.ClientCredentialsTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.zchu.log.Logger;

import java.io.IOException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class Oauth2Interceptor implements Interceptor{
    private static final String HEADER_OAUTH_TOKEN = "Authorization";
    private static final String K_AUTH_TOKEN = "k_auth_token";
    private static final String K_EXPIRATION_TIME = "k_expiration_time";
    private static final String AUTH_ENCODE_KEY = "eddc168_mmx"; //DES加密的KEY

    private SharedPreferences mPreferences;
    private String mBaseUrl;

    public Oauth2Interceptor(Context context, String mBaseUrl) {
        mPreferences = context.getSharedPreferences("Oauth", Context.MODE_PRIVATE);
        this.mBaseUrl = mBaseUrl;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .addHeader(HEADER_OAUTH_TOKEN,getOAuthToken())
                .build();
        Response response = chain.proceed(request);
        if (response.code() == 401 || response.code() == 402) {//授权失败说明OAUTH TOKEN已过期
            mPreferences.edit().remove(K_AUTH_TOKEN).apply();
            Request reRequest = chain.request()
                    .newBuilder()
                    .addHeader(HEADER_OAUTH_TOKEN, getOAuthToken())
                    .build();
            response = chain.proceed(reRequest);
        }
        return response;

    }

    private void saveAuthToken(String authToken, long expiresIn) {
        String userDES = DES.encode(AUTH_ENCODE_KEY, authToken); //先DES加密
        byte[] authBase64 = Base64.encode(userDES.getBytes(), Base64.DEFAULT);//在Base64编码
        mPreferences.
                edit()
                .putString(K_AUTH_TOKEN, new String(authBase64))
                .putLong(K_EXPIRATION_TIME, System.currentTimeMillis() + expiresIn * 1000)
                .apply();
    }

    /**
     * 获取authToken,如果没有就去远程取并存至本地
     *
     * @return authToken
     * @throws IOException
     */
    private String getOAuthToken() throws IOException{

        long expirationTime = mPreferences.getLong(K_EXPIRATION_TIME, -1);
        String auth = mPreferences.getString(K_AUTH_TOKEN, null);
        String authToken = null;
        if(auth != null){
            String authDES = new String(Base64.decode(auth, Base64.DEFAULT));//Base64解码
            authToken = DES.decode(AUTH_ENCODE_KEY, authDES);//DES解密
        }
        if (expirationTime > 0 && System.currentTimeMillis() < expirationTime && authToken != null) {
            return authToken;
        } else{
            NetHttpTransport.Builder builder = new NetHttpTransport.Builder();
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
            builder.setSslSocketFactory(sslSocketFactory);
            NetHttpTransport httpTransport = builder.build();
            TokenResponse response = new ClientCredentialsTokenRequest(httpTransport,
                    new GsonFactory(), new GenericUrl(mBaseUrl + "/token"))
//                .setRedirectUri("https://client.example.com/rd")
                    .setClientAuthentication(new BasicAuthentication(Constant.AUTH2_ID,
                            Constant.AUTH2_SECRET))
                    .execute();
            authToken = response.getAccessToken();
            Logger.e(authToken);
            Logger.e(Logger.fJson(response.toString()));
            saveAuthToken(authToken, response.getExpiresInSeconds());
        }

        return authToken;
    }
}

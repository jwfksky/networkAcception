package store.yifan.cn.networkacception.http;

import android.content.Entity;
import android.text.TextUtils;
import android.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import store.yifan.cn.networkacception.manager.BaseApplication;
import store.yifan.cn.networkacception.utils.IOUtils;
import store.yifan.cn.networkacception.utils.LogUtils;
import store.yifan.cn.networkacception.utils.StringUtils;
import store.yifan.cn.networkacception.utils.encrypt.TOTP;

/**
 * Created by mwqi on 2014/6/7.
 */
public class HttpHelper {
    private static final int mTimeoutConnection = 10 * 1000;// 设置连接超时时间
    private static final int mTimeoutSocket = 15 * 1000;

    /**
     * get请求，获取返回字符串内容
     */
    public static HttpResult get(String url) {
        HttpGet httpGet = new HttpGet(url);
        setRequestHeader(httpGet, url);
        return execute(url, httpGet);
    }

    /**
     * post请求，获取返回字符串内容
     */
    public static HttpResult post(String url, String value) {
        HttpPost httpPost = new HttpPost(url);
        try {
            StringEntity stringEntity = new StringEntity(value, "utf-8");
            stringEntity.setContentEncoding("utf-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);

            setRequestHeader(httpPost, url);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }

        return execute(url, httpPost);
    }
    /*public static HttpResult post(String url, String value) {
        HttpPost httpPost = new HttpPost(url);
        try {
            StringEntity stringEntity=new StringEntity(value,"utf-8");
            stringEntity.setContentEncoding("utf-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);

            HttpParams httpParameters = new BasicHttpParams();
			*//*HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);*//*
            HttpProtocolParams.setUserAgent(httpParameters, "HBPlatformClient");
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            httpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, mTimeoutSocket);
            httpClient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, mTimeoutConnection);

            setRequestHeader(httpPost, url);
            return  new HttpResult(httpClient.execute(httpPost), httpClient, httpPost);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
      //  return execute(url, httpPost);
    }*/

    /**
     * 下载
     */
    public static HttpResult download(String url) {
        HttpGet httpGet = new HttpGet(url);
        return execute(url, httpGet);
    }

    /**
     * 执行网络访问
     */
    private static HttpResult execute(String url, HttpRequestBase requestBase) {
        boolean isHttps = url.startsWith("https://");//判断是否需要采用https
        AbstractHttpClient httpClient = HttpClientFactory.create(isHttps);
        HttpContext httpContext = new SyncBasicHttpContext(new BasicHttpContext());
        HttpRequestRetryHandler retryHandler = httpClient.getHttpRequestRetryHandler();//获取重试机制
        int retryCount = 0;
        boolean retry = true;
        while (retry) {
            try {
                HttpResponse response = httpClient.execute(requestBase, httpContext);//访问网络
                if (response != null) {
                    return new HttpResult(response, httpClient, requestBase);
                }
            } catch (Exception e) {
                IOException ioException = new IOException(e.getMessage());
                retry = retryHandler.retryRequest(ioException, ++retryCount, httpContext);//把错误异常交给重试机制，以判断是否需要采取从事
                LogUtils.e(e);
            }
        }
        return null;
    }

    /**
     * http的返回结果的封装，可以直接从中获取返回的字符串或者流
     */
    public static class HttpResult {
        private HttpResponse mResponse;
        private InputStream mIn;
        private String mStr;
        private HttpClient mHttpClient;
        private HttpRequestBase mRequestBase;

        public HttpResult(HttpResponse response, HttpClient httpClient, HttpRequestBase requestBase) {
            mResponse = response;
            mHttpClient = httpClient;
            mRequestBase = requestBase;
        }

        public int getCode() {
            StatusLine status = mResponse.getStatusLine();
            return status.getStatusCode();
        }

        /**
         * 从结果中获取字符串，一旦获取，会自动关流，并且把字符串保存，方便下次获取
         */
        public String getString() {
            if (!StringUtils.isEmpty(mStr)) {
                return mStr;
            }

            InputStream inputStream = getInputStream();
            ByteArrayOutputStream out = null;
            if (inputStream != null) {
                try {
                    out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int len = -1;
                    while ((len = inputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    byte[] data = out.toByteArray();
                    mStr = new String(data, "utf-8");
                } catch (Exception e) {
                    LogUtils.e(e);
                } finally {
                    IOUtils.close(out);
                    close();
                }
            }


            return mStr;
        }

        /**
         * 获取流，需要使用完毕后调用close方法关闭网络连接
         */
        public InputStream getInputStream() {
            if (mIn == null && getCode() < 300) {
                HttpEntity entity = mResponse.getEntity();

                try {
                    mIn = entity.getContent();
                } catch (Exception e) {
                    LogUtils.e(e);
                }
            }
            return mIn;
        }

        /**
         * 关闭网络连接
         */
        public void close() {
            if (mRequestBase != null) {
                mRequestBase.abort();
            }
            IOUtils.close(mIn);
            if (mHttpClient != null) {
                mHttpClient.getConnectionManager().closeExpiredConnections();
            }
        }

    }

    public static void setRequestHeader(HttpUriRequest request, String serverUrl) {
        request.addHeader("X-Requested-With", "XMLHttpRequest");
        request.addHeader("ClientApp-ID", "8800000001");
        request.addHeader("ClientApp-Ver", "1.0");
        request.addHeader("signature", getSignatureNumber());

        //添加加密算法的自定义头

        String nonce = randomStr(15);
        String timestamp = System.currentTimeMillis() + "";


        String key = "GBVDOMJTOVTXMYTQPB4GMNBRHF2XQ43Q";//定义安全码

        //处理Url，转为小写，并编码
        String signinUrl = serverUrl.toLowerCase();
        try {
            signinUrl = URLEncoder.encode(signinUrl, "utf-8");
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(e);
        }
        signinUrl = signinUrl.toLowerCase();
        //进行排序
        String[] sortArr = new String[]{"appid=8800000001", "nonce=" + nonce, "timestamp=" + timestamp, "requrl=" + signinUrl};
        String[] sortRes = sortByChar(sortArr);
        //HMAC512加密
        String valUrl = String.format("%s&%s&%s&%s",
                sortRes[0],
                sortRes[1],
                sortRes[2],
                sortRes[3]);

        String signature = null;
        try {
            signature = encryptHMAC(valUrl, key);
        } catch (Exception e) {
            LogUtils.e(e);
        }
        signature = signature.replaceAll("\n", "");
        //声明自定义头
        String value = String.format("HBX %s;%s;%s", nonce, timestamp, signature);
        request.addHeader("Authorization", value);

        if (TextUtils.isEmpty(BaseApplication.getAcceptToken())) {
            request.addHeader("Access-Token", "");

        } else {
            request.addHeader("Access-Token", BaseApplication.getAcceptToken());
        }
    }

    //获取动态码
    private static String getSignatureNumber() {
        long interval = System.currentTimeMillis() / 1000;
        System.out.println("interval = " + interval);
        long T0 = 0;
        long X = 30;
        long T = (interval - T0) / X;
        System.out.println("T = " + T);
        String steps = Long.toHexString(T).toUpperCase();
        String secret = "GBVDOMJTOVTXMYTQPB4GMNBRHF2XQ43Q";
        String encryptedPwd = TOTP.generateTOTP(secret,
                steps, "6", "HmacSHA1");
        System.out.println("encryptedPwd = " + encryptedPwd);
        return encryptedPwd;
    }

    //生成15位的随机字符串
    public static String randomStr(int len) {
        String baseStr = "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ!@#$%";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < len; i++) {
            int flag = random.nextInt(baseStr.length());
            sb.append(baseStr.charAt(flag));
        }

        return sb.toString();
    }

    //按照字符进行排序
    public static String[] sortByChar(String[] oral) {
        Arrays.sort(oral);
        return oral;
    }

    //进行HMAC512加密,并返回Base64加密的字符串
    public static String encryptHMAC(String data, String key) throws Exception {

        //SecretKey secretKey = new SecretKeySpec(decryptBASE64(key), KEY_MAC);
        byte[] mData = data.getBytes("utf-8");
        SecretKey secretKey = new SecretKeySpec(key.getBytes("utf-8"), "HmacSHA512");
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        byte[] aBuffer = mac.doFinal(mData);

		/*BASE64Encoder base=new BASE64Encoder();
		String result=base.encodeBuffer(aBuffer);*/
        String result = Base64.encodeToString(aBuffer, Base64.DEFAULT);
        return result;

    }
}

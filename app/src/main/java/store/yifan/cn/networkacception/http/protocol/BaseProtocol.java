package store.yifan.cn.networkacception.http.protocol;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import store.yifan.cn.appstore.R;
import store.yifan.cn.networkacception.http.HttpHelper;
import store.yifan.cn.networkacception.manager.BaseApplication;
import store.yifan.cn.networkacception.manager.Constants;
import store.yifan.cn.networkacception.utils.FileUtils;
import store.yifan.cn.networkacception.utils.IOUtils;
import store.yifan.cn.networkacception.utils.LogUtils;
import store.yifan.cn.networkacception.utils.StringUtils;
import store.yifan.cn.networkacception.utils.UIUtils;

/**
 * Created by mwqi on 2014/6/7.
 */
public abstract class BaseProtocol<Data> {
    public static final String cachePath = "";
    private Gson gson = null;
    public static final String POST="post";
    public static final String GET="get";

    /**
     * 加载协议
     */
    public Data load(String url,String method) {

        String json = null;
        // 1.从本地缓存读取数据，查看缓存时间
        json = loadFromLocal(url);
        // 2.如果缓存时间过期，从网络加载
        if (StringUtils.isEmpty(json)) {
            HttpHelper.HttpResult result = loadFromNet(url,method);
            if (result == null) {
                // 网络出错
                return null;
            } else {
                // 3.判断返回码，对返回码进行处理
                //saveToLocal(json, url);
                int status=result.getCode();
                if (status == Constants.HTTP_STATUSCODE_200) {// 响应成功
                    json=result.getString();
                } else if (status == Constants.HTTP_STATUSCODE_400) {// 响应失败
                    json=result.getString();
                } else if (status == Constants.HTTP_STATUSCODE_401) {// 表示需要登录
                    json = "{'" + UIUtils.getString(R.string.HttpResult)
                            + "':'-1','Msg':'"
                            + UIUtils.getString(R.string.HTTP_STATUSCODE_401_JSON)
                            + "'}";
                } else if (status == Constants.HTTP_STATUSCODE_2000) {// 没有激活当前登入用户
                    json = "{'" + UIUtils.getString(R.string.HttpResult)
                            + "':'-1','Msg':'"
                            + UIUtils.getString(R.string.HTTP_STATUSCODE_2000_JSON)
                            + "'}";

                } else if (status == Constants.HTTP_STATUSCODE_2001) {// 登录成功，您访问此功能的权限不足
                    json = "{'" + UIUtils.getString(R.string.HttpResult)
                            + "':'-1','Msg':'"
                            + UIUtils.getString(R.string.HTTP_STATUSCODE_2001_JSON)
                            + "'}";
                } else if (status == Constants.HTTP_STATUSCODE_2002) {// 客户端没通过验证，请先验证或者更新最新版本
                    json = "{'" + UIUtils.getString(R.string.HttpResult)
                            + "':'-1','Msg':'"
                            + UIUtils.getString(R.string.HTTP_STATUSCODE_2002_JSON)
                            + "'}";
                } else if (status == Constants.HTTP_STATUSCODE_500) {// 服务器异常
                    json = "{'" + UIUtils.getString(R.string.HttpResult)
                            + "':'-1','Msg':'"
                            + UIUtils.getString(R.string.HTTP_STATUSCODE_500_JSON)
                            + "'}";
                } else {
                     json=result.getString();
                }
            }
        }
        return parseFromJson(json,url);
    }

    /**
     * 从本地加载协议
     */
    protected String loadFromLocal(String url) {
        String path = FileUtils.getCacheDir();
        String key[]=url.split("/");
        BufferedReader reader = null;
        try {
            File file=new File(path + key[key.length-1] + "_" + getParames());
            if(!file.exists())return "";
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();// 第一行是时间
            Long time = Long.valueOf(line);
            if (time > System.currentTimeMillis()) {//如果时间未过期
                StringBuilder sb = new StringBuilder();
                String result;
                while ((result = reader.readLine()) != null) {
                    sb.append(result);
                }
                return sb.toString();
            }
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(reader);
        }
        return null;
    }

    /**
     * 从网络加载协议
     */
    protected HttpHelper.HttpResult loadFromNet(String url,String method) {
        HttpHelper.HttpResult result = null;
        if(GET.endsWith(method)){
            HttpHelper.HttpResult httpResult = HttpHelper.get(url + getParames());
            if (httpResult != null) {
                result = httpResult;
                //httpResult.close();
            }
        }else if(POST.endsWith(method)){
            try {
                HttpHelper.HttpResult httpResult=HttpHelper.post(url,getParames());

                if (httpResult != null) {
                    result = httpResult;
                   // httpResult.close();
                }
            } catch (Exception e) {
                LogUtils.e(e);
                return result;
            }
        }

        return result;
    }

    /**
     * 保存到本地
     */
    protected void saveToLocal(String str, String url) {
        String path = FileUtils.getCacheDir();
        String key[]=url.split("/");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path + key[key.length-1] + "_"+ getParames()));
            long time = System.currentTimeMillis() + 1000 * 60;//先计算出过期时间，写入第一行
            writer.write(time + "\r\n");
            writer.write(str.toCharArray());
            writer.flush();
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(writer);
        }
    }

    public synchronized Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    /**
     * 需要增加的额外参数
     */
    protected abstract String getParames();

    protected synchronized String wrapParames(String method,Map<String,String> map) {
        String result="";
        if(GET.equals(method)){
            for(Map.Entry<String,String> entry : map.entrySet()){
                if(!result.contains("?")){
                    result+="?"+entry.getKey()+"="+entry.getValue();
                }else{
                    result+="&"+entry.getKey()+"="+entry.getValue();
                }
            }
        }else{
            JSONObject jsonParams=new JSONObject();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                try {
                    jsonParams.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                    LogUtils.e(e.toString());
                    return result;
                }
            }
            result=jsonParams.toString();
        }
        return result;
    }

    /**
     * 该协议的访问地址(本地缓存标示名)
     */
   // protected abstract String getKey();

    /**
     * 从json中解析
     */
    protected abstract Data parseFromJson(String json,String url);
}

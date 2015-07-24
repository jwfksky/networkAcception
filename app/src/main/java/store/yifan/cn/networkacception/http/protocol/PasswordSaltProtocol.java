package store.yifan.cn.networkacception.http.protocol;

import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import store.yifan.cn.appstore.R;
import store.yifan.cn.networkacception.manager.Constants;
import store.yifan.cn.networkacception.utils.LogUtils;
import store.yifan.cn.networkacception.utils.UIUtils;
import store.yifan.cn.networkacception.utils.encrypt.Base64;
import store.yifan.cn.networkacception.utils.encrypt.DigestUtils;
import store.yifan.cn.networkacception.utils.encrypt.Hmac;

/**
 * Author: Jwf(feijia101@gmail.com) <br\>
 * Date: 2015-07-09 16:07<br\>
 * Version: 1.0<br\>
 * Desc:<br\>
 * Revise:<br\>
 */
public class PasswordSaltProtocol extends BaseProtocol {
    @Override
    protected String getParames() {
        HashMap<String,String> map=new HashMap<>();
        map.put("userCode", Constants.USER_NAME);
        return wrapParames(POST,map);
    }

    @Override
    protected HashMap<String,String> parseFromJson(String json,String url) {
        HashMap<String,String> map=new HashMap<>();
        try {

            JSONObject jsonObject = new JSONObject(json);
            String result=jsonObject.optString("result");
            String msg=jsonObject.optString("msg");
            if("0".equals(result)) {

                map.put("salt",jsonObject.getString("salt"));
                map.put("key",jsonObject.getString("key"));

            }


        } catch (Exception e) {
           e.printStackTrace();
        }
        return map;
    }
}

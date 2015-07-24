package store.yifan.cn.networkacception.http.protocol;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import store.yifan.cn.appstore.R;
import store.yifan.cn.networkacception.manager.BaseApplication;
import store.yifan.cn.networkacception.manager.Constants;
import store.yifan.cn.networkacception.utils.CommonUtils;
import store.yifan.cn.networkacception.utils.LogUtils;
import store.yifan.cn.networkacception.utils.UIUtils;
import store.yifan.cn.networkacception.utils.encrypt.Base64;
import store.yifan.cn.networkacception.utils.encrypt.DigestUtils;
import store.yifan.cn.networkacception.utils.encrypt.Hmac;

/**
 * Author: Jwf(feijia101@gmail.com) <br\>
 * Date: 2015-07-10 14:35<br\>
 * Version: 1.0<br\>
 * Desc:<br\>
 * Revise:<br\>
 */
public class AccessTokenProtocol extends BaseProtocol {
    private String encryptedKey;
    public AccessTokenProtocol(){};
    public AccessTokenProtocol(String encryptedKey) {
        this.encryptedKey=encryptedKey;
    }

    @Override
    protected String getParames() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("grant_type", "password");
        map.put("userCode", Constants.USER_NAME);
        map.put("encryptedPWD", encryptedKey);
        return wrapParames(BaseProtocol.POST,map);
    }

    @Override
    protected HashMap<String,String> parseFromJson(String json, String url) {
        String result="";
        HashMap map= (HashMap) CommonUtils.parserToMap(json);
        try {
            JSONObject obj=new JSONObject(json);
            result=obj.optString("Result");
            if (result.equals("0")) {
                BaseApplication.setAcceptToken(obj.optString("AccessToken"));
                return map;
                }
        } catch (JSONException e) {
            LogUtils.e(e);
        }

        return map;
    }
}

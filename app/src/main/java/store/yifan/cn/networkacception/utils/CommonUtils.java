package store.yifan.cn.networkacception.utils;

import android.text.TextUtils;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Author: Jwf(feijia101@gmail.com) <br\>
 * Date: 2015-07-14 11:23<br\>
 * Version: 1.0<br\>
 * Desc:<br\>
 * Revise:<br\>
 */
public class CommonUtils {
    public static Map parserToMap(String s) {
        Map map = new HashMap();
        if(TextUtils.isEmpty(s))return null;
        JSONObject json = null;
        try {
            json = new JSONObject(s);

            Iterator keys = json.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String value = json.get(key).toString();
                if (value.startsWith("{") && value.endsWith("}")) {
                    map.put(key, parserToMap(value));
                } else {
                    map.put(key, value);
                }

            }
        } catch (JSONException e) {
            LogUtils.e(e);
        }
        return map;
    }
}

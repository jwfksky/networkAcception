package store.yifan.cn.networkacception.http.protocol;

import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import store.yifan.cn.networkacception.bean.BillBean;
import store.yifan.cn.networkacception.manager.Constants;
import store.yifan.cn.networkacception.utils.LogUtils;
import store.yifan.cn.networkacception.utils.UIUtils;

/**
 * Author: Jwf(feijia101@gmail.com) <br\>
 * Date: 2015-07-13 10:02<br\>
 * Version: 1.0<br\>
 * Desc:<br\>
 * Revise:<br\>
 */
public class BillListProtocol extends BaseProtocol<List<BillBean>> {

    private HashMap<String, String> params;
    private String startedAt;
    private String endedAt;

    public BillListProtocol(HashMap<String, String> params) {
        this.params = params;
    }

    @Override
    protected String getParames() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("mobileUserId", "223");
        map.put("startedAt", params.get("mSearchStartTime"));
        map.put("endedAt", params.get("mSearchEndTime"));
        map.put("currentPage", params.get("currentPage"));
        map.put("queryKey", params.get("queryKey"));
        map.put("pageSize", "10");
        map.put("sortName", "");
        map.put("sort", params.get("sort"));
        map.put("payFlag", params.get("tag"));
        return wrapParames(POST, map);
    }

    @Override
    protected List<BillBean> parseFromJson(String json, String url) {
        if (json != null) {
            try {
                JSONObject obj = new JSONObject(json);
                String result = obj.optString("result");
                String msg = obj.optString("msg");
                UIUtils.showToastSafe(msg);
                if ("1".equals(result)) {
                    String data = obj.optString("data");
                    Constants.totalPage = Integer.parseInt(obj.optString("totalPage"));
                    return getGson().fromJson(data, new TypeToken<List<BillBean>>() {
                    }.getType());
                } else if ("0".equals(result)) {
                    return new ArrayList<>();
                }
            } catch (JSONException e) {
                LogUtils.e(e);
                return null;
            }

        }

        return null;
    }
}

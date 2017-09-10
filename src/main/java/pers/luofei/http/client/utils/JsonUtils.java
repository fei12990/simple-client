package pers.luofei.http.client.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by luofei on 2017/9/6.
 */
public class JsonUtils {

    public static <T extends JSON> Object parse(T json, String node) {

        if (json == null) {
            return null;
        }
        if (StringUtils.isEmpty(node)) {
            return json;
        }

        Object tmp = json;
        for (String s : node.split("/")) {
            if (!StringUtils.isEmpty(s)) {
                if (!(tmp instanceof JSONObject)) {
                    return null;
                }
                tmp = ((JSONObject)tmp).get(s);
                if (tmp == null) {
                    return null;
                }
            }
        }
        return tmp;
    }
}

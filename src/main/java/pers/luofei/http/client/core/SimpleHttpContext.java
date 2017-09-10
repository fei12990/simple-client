package pers.luofei.http.client.core;

import pers.luofei.http.client.codec.DefaultResponseBodyDecoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luofei on 2017/9/6.
 */
public class SimpleHttpContext {

    public static String DEFAULT_CHARSET = "UTF-8";

    /**
     * 默认的文件下载路径<br/>
     * 如果该值没有设置，{@link DefaultResponseBodyDecoder}将使用系统的临时目录
     */
    public static final String KEY_FILE_DOWNLOAD_PATH = "rsp.decode.file.path";

    /**
     * 下载进度通知的间隔时间<br/>
     * {@link DefaultResponseBodyDecoder}中默认为500ms
     */
    public static final String KEY_FILE_DOWNLOAD_NOTIFY_INTERVAL = "rsp.decode.file.notify.interval";

    public static final Map<String, Object> properties = new HashMap<>();

    public static Object put(String key, Object value) {

        return properties.put(key, value);
    }

    public static int getInt(String key, int defaultValue) {

        Object v = properties.get(key);
        if (v == null) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(v.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static String getString(String key) {

        Object v = properties.get(key);
        return v == null ? null : v.toString();
    }

    public static long getLong(String key, long defaultValue) {

        Object v = properties.get(key);
        if (v == null) {
            return defaultValue;
        }
        try {
            return Long.valueOf(v.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

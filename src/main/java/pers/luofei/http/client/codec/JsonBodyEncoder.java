package pers.luofei.http.client.codec;

import com.alibaba.fastjson.JSON;
import pers.luofei.http.client.core.SimpleHttpContext;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luofei on 2017/9/8.
 */
public class JsonBodyEncoder implements RequestBodyEncoder {

    @Override
    public List<? extends InputStream> encode(Map<String, Object> requestParameters) throws CodecException {

        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, Object> entry : requestParameters.entrySet()) {
            Object v = entry.getValue();
            if (v instanceof File || v instanceof InputStream) {
                continue;
            }
            params.put(entry.getKey(), v.toString());
        }
        try {
            return Arrays.asList(new ByteArrayInputStream(JSON.toJSONString(params).getBytes(SimpleHttpContext.DEFAULT_CHARSET)));
        } catch (UnsupportedEncodingException e) {
            throw new CodecException("Encoding json failed.", e);
        }
    }
}

package pers.luofei.http.client.codec;

import pers.luofei.http.client.core.SimpleHttpContext;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by luofei on 2017/9/8.
 */
public class UrlEncodedBodyEncoder implements RequestBodyEncoder {

    @Override
    public List<? extends InputStream> encode(Map<String, Object> requestParameters) throws CodecException {

        if (requestParameters.isEmpty()) {
            return null;
        }

        StringBuilder tmp = new StringBuilder();
        for (Map.Entry<String, Object> entry : requestParameters.entrySet()) {
            Object v = entry.getValue();
            if (v instanceof File || v instanceof InputStream) {
                continue;
            }
            tmp.append(entry.getKey());
            tmp.append("=");
            tmp.append(v.toString());
            tmp.append("&");
        }
        try {
            return Arrays.asList(new ByteArrayInputStream(tmp.toString().getBytes(SimpleHttpContext.DEFAULT_CHARSET)));
        } catch (UnsupportedEncodingException e) {
            throw new CodecException(e);
        }
    }
}

package pers.luofei.http.client.codec;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by luofei on 2017/9/8.
 */
public interface RequestBodyEncoder {

    List<? extends InputStream> encode(Map<String, Object> requestParameters) throws CodecException;
}

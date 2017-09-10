package pers.luofei.http.client.codec;

import pers.luofei.http.client.core.RequestMethodInfo;

import java.io.InputStream;

/**
 *
 * @author luofei on 2017/9/4.
 */
public interface ResponseBodyDecoder {

    Object decode(RequestMethodInfo methodInfo, int responseCode, InputStream in) throws Exception;
}

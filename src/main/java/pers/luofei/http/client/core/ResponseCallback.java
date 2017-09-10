package pers.luofei.http.client.core;

import pers.luofei.http.client.annotations.ResponseContentType;
import pers.luofei.http.client.codec.DefaultResponseBodyDecoder;

/**
 * 回调类，用于处理每个请求的结果。
 *
 * Created by luofei on 2017/9/6.
 */
public interface ResponseCallback {

    /**
     * http请求错误<br/>
     * 当http code不是200时，触发该函数
     *
     * @param result 返回结果的body
     * @param code http response code
     */
    void httpFailed(Object result, int code);

    /**
     * 请求成功<br/>
     *
     * @param result 返回结果的body
     */
    void success(Object result);

    /**
     * http请求成功，但是业务操作失败。<br/>
     * 当开发者配置了{@link ResponseContentType#condition()}时，
     * {@link DefaultResponseBodyDecoder}会根据配置的condition校验结果，如果不满足条件，则会触发该函数
     *
     * @param result 返回结果的body
     */
    void failed(Object result);

    /**
     * 数据下载进度，默认500ms触发一次。
     *
     * @param total 总数据量。当返回头中没有设置Content-Length时，该值为<code>-1</code>
     * @param transferred  已下载数据量
     */
    void onProcess(long total, long transferred);
}

package pers.luofei.http.client.core;

import pers.luofei.http.client.annotations.RequestContentType;
import pers.luofei.http.client.annotations.RequestId;
import pers.luofei.http.client.annotations.RequestMapping;
import pers.luofei.http.client.annotations.ResponseContentType;
import pers.luofei.http.client.codec.ResponseBodyDecoder;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求方法的基础信息<br>
 * 在使用自定义decoder时，参数中会带有相应的{@link RequestMethodInfo}，
 * 自定义decoder可根据该值进行具体的decode操作
 *
 * @see ResponseBodyDecoder
 * @author luofei on 2017/9/5.
 */
public class RequestMethodInfo {

    /**
     * 请求方法的ID，可以使用{@link RequestId}指定
     */
    private String id;

    /**
     * 请求方法的方法名
     */
    private String name;

    /**
     * 请求的完整uri,由类注解{@link RequestMapping}和方法注解{@link RequestMapping}的值拼接起来
     */
    private String uri;

    /**
     * 请求的Content-Type,由{@link RequestContentType}指定
     */
    private ContentType contentType;

    /**
     * 相应的Content-Type,由{@link ResponseContentType}指定
     * 用于decoder解析
     */
    private ContentType responseContentType;

    /**
     * 指定解析的节点<br>
     * 示例：
     * 在返回的json数据：
     * {@code
     * {
     *     responseCode:200,
     *     data:[
     *         key:"name",
     *         value:"123"
     *     ]
     * }}中，指定decodeNode="/data",那么decoder将只解析data部分的数据，再根据returnType生成相应的对象
     */
    private String decodeNode;

    /**
     * 请求的HTTP方法，由{@link RequestMapping}的method值指定，默认为GET
     */
    private String requestMethod;

    /**
     * 返回数据解析后的Type，用于decoder解析
     */
    private Type returnType;

    /**
     * 方法参数的Class
     */
    private List<Class<?>> parameterTypes;

    /**
     * 回调实现类
     */
    private ResponseCallback callback;

    /**
     * 校验条件
     */
    private String[] condition;

    /**
     * 返回数据长度
     */
    private long responseContentLength;

    private Map<String,List<String>> responseHeaders;

    /**
     * 返回的内容编码
     */
    private String contentEncoding;

    public String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    void setUri(String uri) {
        this.uri = uri;
    }

    public ContentType getContentType() {
        return contentType;
    }

    void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public ContentType getResponseContentType() {
        return responseContentType;
    }

    void setResponseContentType(ContentType responseContentType) {
        this.responseContentType = responseContentType;
    }

    public String getDecodeNode() {
        return decodeNode;
    }

    void setDecodeNode(String decodeNode) {
        this.decodeNode = decodeNode;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Type getReturnType() {
        return returnType;
    }

    void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public List<Class<?>> getParameterTypes() {
        return parameterTypes;
    }

    void setParameterTypes(List<Class<?>> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public ResponseCallback getCallback() {
        return callback;
    }

    void setCallback(ResponseCallback callback) {
        this.callback = callback;
    }

    public String[] getCondition() {
        return condition;
    }

    void setCondition(String[] condition) {
        this.condition = condition;
    }

    public long getResponseContentLength() {
        return responseContentLength;
    }

    void setResponseContentLength(long contentLength) {
        this.responseContentLength = contentLength;
    }

    public Map<String, List<String>> getResponseHeaders() {
        if(responseHeaders == null){
            responseHeaders = new HashMap<>();
        }
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, List<String>> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }
}

package pers.luofei.http.client.core;

import pers.luofei.http.client.annotations.RequestMapping;
import pers.luofei.http.client.codec.DefaultResponseBodyDecoder;
import pers.luofei.http.client.codec.ResponseBodyDecoder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by luofei on 2017/8/31.
 */

public class RequestMetaInfo {

    public static ResponseBodyDecoder DEFAULT_BODY_DECODER;
    private static final String SPLITER_URL = "/";

    private String host;

    private HttpProtocol protocol = HttpProtocol.HTTP;
    private String uri = "";
    private Map<String, String> defaultHeaders;
    private Map<Method, MethodInfo> methodInfoList;

    public RequestMetaInfo() {
        this.defaultHeaders = new HashMap<>();
        this.methodInfoList = new HashMap<>();
        DEFAULT_BODY_DECODER = new DefaultResponseBodyDecoder();
    }

    class MethodInfo {

        String id;
        String name;
        String uri = "";
        String method = RequestMapping.GET;
        ContentType contentType = ContentType.FORM_URLENCODED;
        List<Class<? extends Annotation>> methodAnnotations = new ArrayList<>();
        List<Class<? extends Annotation>> parameterAnnotations = new ArrayList<>();
        List<Class<?>> parameterTypes = new ArrayList<>();

        Type returnType;
        ContentType responseContentType = ContentType.JSON;
        String decodeNode;
        ResponseCallback callback = DefaultCallback.DEFAULT_CALLBACK;
        String[] condition = {};

        public void addMethodAnnotations(Class<? extends Annotation> methodAnnotation) {
            if (methodAnnotation == null) {
                return;
            }
            this.methodAnnotations.add(methodAnnotation);
        }

        public void addParameterAnnotations(Class<? extends Annotation> parameterAnnotation) {
            if (parameterAnnotation == null) {
                return;
            }
            this.parameterAnnotations.add(parameterAnnotation);
        }

        public void addParameterTypes(Class<?>[] parameterTypes) {

            if (parameterTypes == null) {
                return;
            }
            this.parameterTypes.addAll(Arrays.asList(parameterTypes));
        }

        public void setUri(String uri) {
            if (uri.isEmpty()) {
                return;
            }
            if (uri.endsWith(SPLITER_URL)) {
                this.uri = uri;
            } else {
                this.uri = uri + SPLITER_URL;
            }
        }

        public String getFullUri() {
            return RequestMetaInfo.this.getUri() + uri;
        }

        public RequestMethodInfo toRequestMethodInfo() {

            RequestMethodInfo info = new RequestMethodInfo();
            info.setId(id);
            info.setName(name);
            info.setContentType(contentType);
            info.setUri(RequestMetaInfo.this.getUri() + uri);
            info.setRequestMethod(method);
            info.setReturnType(returnType);
            info.setParameterTypes(parameterTypes);
            info.setResponseContentType(responseContentType);
            info.setDecodeNode(decodeNode);
            info.setCallback(callback);
            info.setCondition(condition);
            return info;
        }
    }

    MethodInfo getMethodInfo(Method method) {

        return methodInfoList.get(method);
    }

    public MethodInfo addNewMethodInfo(Method method) {

        if (method == null) {
            return null;
        }
        MethodInfo old = methodInfoList.get(method);
        if (old != null) {
            return old;
        }
        MethodInfo info = new MethodInfo();
        info.name = method.getName();
        methodInfoList.put(method, info);
        return info;
    }

    public String getUrl(Method method) {

        MethodInfo info = methodInfoList.get(method);
        if (info.uri.toLowerCase().startsWith("http://")) {
            protocol = HttpProtocol.HTTP;
            return info.uri;
        }
        if (info.uri.toLowerCase().startsWith("https://")) {
            protocol = HttpProtocol.HTTPS;
            return info.uri;
        }
        return protocol.name() + "://" + host + "/" + uri + info.uri;
    }

    public List<Class<? extends Annotation>> getParameterAnnotations(Method method) {

        MethodInfo info = methodInfoList.get(method);
        return info.parameterAnnotations;
    }

    public List<Class<? extends Annotation>> getMethodAnnotations(Method method) {

        MethodInfo info = methodInfoList.get(method);
        return info.methodAnnotations;
    }

    public ContentType getContentType(Method method) {

        MethodInfo info = methodInfoList.get(method);
        return info.contentType;
    }

    public ContentType getResponseContentType(Method method) {

        MethodInfo info = methodInfoList.get(method);
        return info.responseContentType;
    }

    public Type getReturnType(Method method) {

        MethodInfo info = methodInfoList.get(method);
        return info.returnType;
    }

    public String getRequestMethod(Method method) {

        MethodInfo info = methodInfoList.get(method);
        return info.method;
    }

    public ResponseCallback getCallback(Method method) {

        MethodInfo info = methodInfoList.get(method);
        return info.callback;
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public HttpProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(HttpProtocol protocol) {
        this.protocol = protocol;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        if (uri.isEmpty()) {
            return;
        }
        if (uri.endsWith(SPLITER_URL)) {
            this.uri = uri;
        } else {
            this.uri = uri + SPLITER_URL;
        }
    }

    public Map<String, String> getDefaultHeaders() {
        return defaultHeaders;
    }

    public void setDefaultHeaders(Map<String, String> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
    }

    public void addDefaultHeaders(String key, String value) {

        if (key == null || value == null) {
            return;
        }
        this.defaultHeaders.put(key, value);
    }
}

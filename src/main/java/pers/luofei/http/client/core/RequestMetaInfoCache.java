package pers.luofei.http.client.core;

import pers.luofei.http.client.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by luofei on 2017/8/31.
 */
public final class RequestMetaInfoCache {

    private static Map<Class<?>, RequestMetaInfo> metaInfoCache;

    public static String REQUEST_ADDRESS;

    private static Map<Class<? extends ResponseCallback>, ResponseCallback> callbackCache;

    private static Map<Class<? extends RequestHostDiscovery>, RequestHostDiscovery> hostDiscoveryCache;

    static {
        REQUEST_ADDRESS = "";
        metaInfoCache = new HashMap<>();
        callbackCache = new HashMap<>();
        hostDiscoveryCache = new HashMap<>();
    }

    public static void setRequestAddress(String requestAddress) {
        REQUEST_ADDRESS = requestAddress;
    }

    static RequestMetaInfo get(Class<?> clazz) throws InstantiationException, IllegalAccessException {

        if (clazz == null) {
            return null;
        }
        RequestMetaInfo metaInfo = metaInfoCache.get(clazz);
        if (metaInfo == null) {
            metaInfo = parse(clazz);
            metaInfoCache.put(clazz, metaInfo);
        }

        return metaInfo;
    }

    private static RequestMetaInfo parse(Class<?> clazz) {

        RequestMetaInfo metaInfo = new RequestMetaInfo();
        for (Annotation annotation : clazz.getAnnotations()) {
            if (annotation.annotationType() == RequestProtocol.class) {
                metaInfo.setProtocol(((RequestProtocol) annotation).value());
            } else if (annotation.annotationType() == RequestMapping.class) {
                metaInfo.setUri(((RequestMapping) annotation).value());
            } else if (annotation.annotationType() == RequestHost.class) {
                Class<? extends RequestHostDiscovery> discoveryClass = ((RequestHost) annotation).hostDiscovery();
                if (discoveryClass == NullRequestHostDiscovery.class) {
                    metaInfo.setHost(((RequestHost) annotation).value());
                } else {
                    RequestHostDiscovery hostDiscovery = hostDiscoveryCache.get(discoveryClass);
                    if (hostDiscovery == null) {
                        try {
                            hostDiscovery = discoveryClass.newInstance();
                            String balanceId = ((RequestHost) annotation).balanceId();
                            metaInfo.setHost(hostDiscovery.getRequestHost(balanceId));
                        } catch (Exception e) {
                            System.err.println("Cannot initialize custom hostDiscovery.");
                            e.printStackTrace();
                        }
                    }
                }
            } else if (annotation.annotationType() == RequestHeaders.class) {
                String tmp = ((RequestHeaders) annotation).value();
                StringTokenizer st = new StringTokenizer(tmp, ":;");
                while (st.hasMoreElements()) {
                    metaInfo.addDefaultHeaders(st.nextToken(), st.nextToken());
                }
            }
        }

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            RequestMetaInfo.MethodInfo methodInfo = metaInfo.addNewMethodInfo(method);
            methodInfo.returnType = method.getGenericReturnType();
            for (Annotation annotation : method.getAnnotations()) {
                methodInfo.addMethodAnnotations(annotation.annotationType());
                if (annotation.annotationType() == RequestMapping.class) {
                    methodInfo.setUri(((RequestMapping) annotation).value());
                    methodInfo.method = ((RequestMapping) annotation).method();
                } else if (annotation.annotationType() == RequestContentType.class) {
                    methodInfo.contentType = ((RequestContentType) annotation).value();
                } else if (annotation.annotationType() == ResponseContentType.class) {
                    methodInfo.responseContentType = ((ResponseContentType) annotation).value();
                    methodInfo.decodeNode = ((ResponseContentType) annotation).node();
                    Class<? extends ResponseCallback> callbackClass = ((ResponseContentType) annotation).callback();
                    ResponseCallback callback = callbackCache.get(callbackClass);
                    if (callback == null) {
                        try {
                            callback = callbackClass.newInstance();
                            callbackCache.put(callbackClass, callback);
                            methodInfo.callback = callback;
                        } catch (Exception e) {
                            System.err.println("Cannot initialize custom callback, use DefaultCallback instead.");
                            e.printStackTrace();
                        }
                    }
                    methodInfo.condition = ((ResponseContentType) annotation).condition();
                } else if (annotation.annotationType() == RequestId.class) {
                    methodInfo.id = ((RequestId) annotation).value();
                }
            }
            for (Annotation[] annotations : method.getParameterAnnotations()) {
                if (annotations.length == 0) {
                    continue;
                }
                Class<? extends Annotation> anno = annotations[0].annotationType();
                methodInfo.addParameterAnnotations(anno);
                if (anno == Callback.class) {
                    //TODO
                }
            }
            methodInfo.addParameterTypes(method.getParameterTypes());
            if (methodInfo.id == null) {
                methodInfo.id = String.valueOf((methodInfo.name + methodInfo.getFullUri() + methodInfo.method).hashCode());
            }
        }
        return metaInfo;
    }

    public static void updateHost(Class<?> clazz, String host) {

        if (host == null) {
            return;
        }

        RequestMetaInfo info;
        try {
            info = get(clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (info == null) {
            return;
        }
        info.setHost(host);
    }
}

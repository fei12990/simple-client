package pers.luofei.http.client.core;

import pers.luofei.http.client.annotations.*;
import pers.luofei.http.client.codec.*;

import javax.net.ssl.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;
import static pers.luofei.http.client.core.ContentType.FORM_DATA;

/**
 * @author luofei[fei12990@foxmail.com]
 * @date 2017/8/31
 */
public class ProxyMethod {

    public static final String DEFAULT_BOUNDRAY = "------" + SimpleHttp.NAME + "BoundaryjT4BOC2D7I70yhYD";

    private static final String CONTENT_TYPE_FORM_DATA;

    public static X509TrustManager x509TrustManager;

    public static HostnameVerifier hostnameVerifier;

    static {

        CONTENT_TYPE_FORM_DATA = "multipart/form-data;boundary=" + DEFAULT_BOUNDRAY;

        // trust all
        x509TrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
    }

    private Map<ContentType, RequestBodyEncoder> encoders;

    ProxyMethod() {

        encoders = new HashMap<>();
        encoders.put(ContentType.JSON, new JsonBodyEncoder());
        encoders.put(ContentType.XML, new XmlBodyEncoder());
        encoders.put(ContentType.FORM_URLENCODED, new UrlEncodedBodyEncoder());
        encoders.put(FORM_DATA, new FormDataEncoder());
        encoders.put(ContentType.OTHER, new OtherTypeBodyEncoder());
    }

    Object execute(RequestMetaInfo requestMetaInfo, Method method, Object[] args) throws Exception {

        return execute0(requestMetaInfo, method, args);
    }


    // TODO 抽象出encoder decoder
    private Object execute0(RequestMetaInfo requestMetaInfo, Method method, Object[] args) throws Exception {

        List<Class<? extends Annotation>> parameterAnnotations = requestMetaInfo.getParameterAnnotations(method);
        Annotation[][] originAnno = method.getParameterAnnotations();
        Class<?>[] parameterTypes = method.getParameterTypes();

        // parse url, headers, parameters or body
        String urlStr = null;
        InputStream requestBody = null;
        Map<String, Object> requestParameters = new HashMap<>();
        Map<String, String> customHeaders = new HashMap<>();
        for (int i = 0; i < parameterAnnotations.size(); i++) {
            Class<? extends Annotation> anno = parameterAnnotations.get(i);
            if (anno == RequestHeaders.class) {
                if (Map.class.isAssignableFrom(parameterTypes[i])) {
                    Map<?, ?> tmp = (Map<?, ?>) args[i];
                    for (Object o : tmp.keySet()) {
                        if (o == null) {
                            continue;
                        }
                        Object v = tmp.get(o);
                        if (v == null) {
                            continue;
                        }
                        //connection.setRequestProperty(o.toString(), v.toString());
                        customHeaders.put((o.toString()), v.toString());
                    }
                } else {
                    String key = ((RequestHeaders) originAnno[i][0]).value();
                    //connection.setRequestProperty(key, args[i].toString());
                    customHeaders.put(key, args[i].toString());
                }
            } else if (anno == RequestParam.class) {
                if (Map.class.isAssignableFrom(parameterTypes[i])) {
                    Map<?, ?> tmp = (Map<?, ?>) args[i];
                    for (Object o : tmp.keySet()) {
                        if (o == null) {
                            continue;
                        }
                        Object v = tmp.get(o);
                        if (v == null) {
                            continue;
                        }
                        requestParameters.put(o.toString(), v);
                    }
                } else {
                    String key = ((RequestParam) originAnno[i][0]).value();
                    if (!key.isEmpty()) {
                        requestParameters.put(key, args[i]);
                    }
                }
            } else if (anno == RequestBody.class) {
                if (InputStream.class.isAssignableFrom(parameterTypes[i])) {
                    requestBody = (InputStream) args[i];
                }
            } else if (anno == Callback.class) {
                if (ResponseCallback.class.isAssignableFrom(parameterTypes[i])) {
                    requestMetaInfo.getMethodInfo(method).callback = (ResponseCallback) args[i];
                }
            } else if (anno == RequestUrl.class) {
                if (String.class == parameterTypes[i]) {
                    urlStr = (String) args[i];
                    //parse url parameters
                    String[] urlAndParams = urlStr.split("\\?", 2);
                    if (urlAndParams.length == 2) {
                        urlStr = urlAndParams[0];
                        for (String kvStr : urlAndParams[1].split("&")) {
                            String[] kv = kvStr.split("=", 2);
                            if (kv.length == 1) {
                                requestParameters.put(kv[0], "");
                            } else {
                                requestParameters.put(kv[0], kv[1]);
                            }
                        }
                    }
                }
            }
        }

        urlStr = urlStr == null ? requestMetaInfo.getUrl(method) : urlStr;
        URL url = new URL(urlStr);
        HttpURLConnection connection;
        if (requestMetaInfo.getProtocol() == HttpProtocol.HTTPS) {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{x509TrustManager}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        }
        connection = (HttpURLConnection) url.openConnection();

        // default headers
        Map<String, String> defaultHeaders = requestMetaInfo.getDefaultHeaders();
        for (Map.Entry<String, String> entry : defaultHeaders.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
        // set request Content-Type
        ContentType contentType = requestMetaInfo.getContentType(method);
        connection.setRequestProperty(ContentType.KEY, contentType.getValue());

        // request method
        connection.setRequestMethod(requestMetaInfo.getRequestMethod(method));


        // set custom headers
        for (Map.Entry<String, String> headers : customHeaders.entrySet()) {
            connection.setRequestProperty(headers.getKey(), headers.getValue());
        }
        if (contentType == FORM_DATA) {
            connection.setRequestProperty(ContentType.KEY, CONTENT_TYPE_FORM_DATA);
        }

        // set parameters
        List<InputStream> requestInputStream = new ArrayList<>();
        if (requestBody != null) {
            requestInputStream.add(requestBody);
        } else {
            List<? extends InputStream> streams = encoders.get(contentType).encode(requestParameters);
            if (streams != null) {
                requestInputStream.addAll(streams);
            }
        }
        OutputStream out;
        if (!requestInputStream.isEmpty()) {
            connection.setDoOutput(true);
            out = connection.getOutputStream();
            byte[] buf = new byte[1024];
            for (InputStream inputStream : requestInputStream) {
                if (inputStream == null) {
                    continue;
                }
                int bytes;
                while ((bytes = inputStream.read(buf)) != -1) {
                    out.write(buf, 0, bytes);
                }
                inputStream.close();
            }
            out.close();
        }

        ResponseBodyDecoder decoder = RequestMetaInfo.DEFAULT_BODY_DECODER;
        Type returnType = requestMetaInfo.getReturnType(method);
        int responseCode = connection.getResponseCode();
        InputStream in = responseCode == HTTP_OK ? connection.getInputStream() : connection.getErrorStream();

        RequestMethodInfo response = requestMetaInfo.getMethodInfo(method).toRequestMethodInfo();
        response.setResponseContentLength(connection.getContentLengthLong());
        Map<String, List<String>> responseHeaders = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
            String k = entry.getKey();
            if (k == null) {
                continue;
            }
            responseHeaders.put(k.toLowerCase(), entry.getValue());
        }
        response.setResponseHeaders(responseHeaders);
        response.getResponseContentType().setValue(connection.getContentType());
        response.setContentEncoding(connection.getContentEncoding());
        Object result = decoder.decode(response, responseCode, in);

        if (InputStream.class != returnType) {
            connection.disconnect();
        }

        return result;
    }
}
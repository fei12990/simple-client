package pers.luofei.http.client.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

import pers.luofei.http.client.SimpleHttpException;
import pers.luofei.http.client.core.ContentType;
import pers.luofei.http.client.core.RequestMethodInfo;
import pers.luofei.http.client.core.ResponseCallback;
import pers.luofei.http.client.core.SimpleHttpContext;
import pers.luofei.http.client.utils.JsonUtils;
import pers.luofei.http.client.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import static java.net.HttpURLConnection.HTTP_OK;
import static pers.luofei.http.client.core.SimpleHttpContext.KEY_FILE_DOWNLOAD_NOTIFY_INTERVAL;
import static pers.luofei.http.client.core.SimpleHttpContext.KEY_FILE_DOWNLOAD_PATH;

/**
 * Created by luofei on 2017/9/4.
 */
public class DefaultResponseBodyDecoder implements ResponseBodyDecoder {

    @Override
    public Object decode(RequestMethodInfo methodInfo, int responseCode, InputStream in) throws Exception {
        in = getActualInputStream(methodInfo, in);
        if (responseCode == HTTP_OK) {
            return decode(methodInfo, in);
        } else {
            long contentLength = methodInfo.getResponseContentLength();
            ResponseCallback responseCallback = methodInfo.getCallback();
            methodInfo.getCallback().httpFailed(new String(readResponse(in, contentLength, responseCallback), SimpleHttpContext.DEFAULT_CHARSET), responseCode);
            return null;
        }
    }

    protected InputStream getActualInputStream(RequestMethodInfo response, InputStream in) throws IOException {
        if ("gzip".equals(response.getContentEncoding())) {
            in = new GZIPInputStream(in);
        }
        return in;
    }

    protected Object decode(RequestMethodInfo methodInfo, InputStream in) throws Exception {

        Type returnType = methodInfo.getReturnType();
        if (InputStream.class == returnType) {
            return in;
        }

        // File download
        if (File.class == returnType) {
            return writeFile(methodInfo, in);
        }

        ContentType contentType = methodInfo.getResponseContentType();
        switch (contentType) {
            case JSON:
                byte[] buf = readResponse(in, methodInfo.getResponseContentLength(), methodInfo.getCallback());
                String decodeNode = methodInfo.getDecodeNode();
                JSON originJson = (JSON) JSON.parse(buf);

                // result validate
                if (!validate(originJson, methodInfo.getCondition())) {
                    methodInfo.getCallback().failed("Result chekc failed:\n" + originJson);
                    return null;
                }

                Object result = JsonUtils.parse(originJson, decodeNode);
                if (result == null) {
                    throw new JSONException("JSON parse failed. no data found for:" + decodeNode);
                }

                if (returnType instanceof ParameterizedType) {
                    Type rawType = ((ParameterizedType) returnType).getRawType();
                    if (rawType == List.class) {
                        Type[] types = ((ParameterizedType) returnType).getActualTypeArguments();
                        return JSON.parseArray(result.toString(), (Class<?>) types[0]);
                    } else {
                        return JSON.toJavaObject((JSON) result, (Class<?>) rawType);
                    }
                } else if (returnType instanceof Class) {
                    if (returnType == List.class) {
                        return JSON.parseArray(result.toString());
                    }
                    return JSON.toJavaObject((JSON) result, (Class<?>) returnType);
                }
                return result;
            case OTHER:
                return new String(readResponse(in, methodInfo.getResponseContentLength(), methodInfo.getCallback()), SimpleHttpContext.DEFAULT_CHARSET);
            default:
                throw new UnsupportedOperationException("Unsupported Response Content Type:" + contentType);
        }
    }

    protected File writeFile(RequestMethodInfo methodInfo, InputStream in) throws IOException, SimpleHttpException {

        String filePath = SimpleHttpContext.getString(KEY_FILE_DOWNLOAD_PATH);
        if (StringUtils.isEmpty(filePath)) {
            filePath = System.getProperty("java.io.tmpdir");
        }
        if (StringUtils.isEmpty(filePath)) {
            throw new SimpleHttpException("File download path not set.");
        }

        String fileName = null;
        List<String> disposition = methodInfo.getResponseHeaders().get("content-disposition");
        if (disposition != null) {
            for (String value : disposition) {
                StringTokenizer st = new StringTokenizer(value, ";=");
                while (st.hasMoreElements()) {
                    if ("filename".equalsIgnoreCase(st.nextToken())) {
                        fileName = st.nextToken();
                        if (fileName.startsWith("\"") && fileName.endsWith("\"")) {
                            fileName = fileName.substring(1, fileName.length() - 1);
                        }
                        break;
                    }
                }
                if (fileName != null) {
                    break;
                }
            }
        }
        if (fileName == null) {
            fileName = String.valueOf(System.currentTimeMillis());
        }

        long contentLength = methodInfo.getResponseContentLength();
        File file = new File(filePath + File.separator + fileName);
        if (!file.getParentFile().exists()) {
            file.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        RandomAccessFile accessFile = new RandomAccessFile(file, "rws");
        FileChannel fc = accessFile.getChannel();
        ByteBuffer bb = ByteBuffer.allocate(8192);
        int processed = 0;
        long lastNotifyTime = 0l;
        long notifyInterval = SimpleHttpContext.getLong(KEY_FILE_DOWNLOAD_NOTIFY_INTERVAL, 500);
        for (int c; (c = in.read(bb.array())) != -1; processed += c) {
            bb.put(bb.array(), 0, c);
            bb.flip();
            fc.write(bb);
            bb.clear();
            long now = System.currentTimeMillis();
            if (now - lastNotifyTime > notifyInterval) {
                methodInfo.getCallback().onProcess(contentLength, processed);
                lastNotifyTime = now;
            }
        }
        fc.close();
        accessFile.close();
        return file;
    }

    protected byte[] readResponse(InputStream in, long contentLength, ResponseCallback responseCallback) throws IOException {

        if (in == null) {
            return new byte[]{};
        }

        List<Object[]> arrayList = new ArrayList<>();
        int length = 0;
        while (true) {
            byte[] buf = new byte[1024];
            int c = in.read(buf);
            if (c <= 0) {
                break;
            }
            length += c;
            arrayList.add(new Object[]{buf, c});
            responseCallback.onProcess(contentLength, length);
        }
        byte[] result = new byte[length];
        int pos = 0;
        for (Object[] data : arrayList) {
            byte[] bytes = (byte[]) data[0];
            System.arraycopy(bytes, 0, result, pos, (int) data[1]);
            pos += (int) data[1];
        }

        return result;
    }

    protected boolean validate(JSON json, String[] conditions) {

        List<Operation> parsed = new ArrayList<>();
        for (String condition : conditions) {
            for (Operation.Operator operator : Operation.Operator.values()) {
                String[] kv = condition.split(operator.flag, 2);
                if (kv.length == 2) {
                    parsed.add(new Operation(kv, operator));
                    break;
                }
            }
        }
        for (Operation operation : parsed) {
            String[] cdt = operation.factors;
            Object tmp = JsonUtils.parse(json, cdt[0]);
            if (tmp instanceof JSON) {
                return false;
            }
            cdt[0] = String.valueOf(tmp);
            if (!operation.calc()) {
                return false;
            }
        }

        return true;
    }
}

class Operation {

    enum Operator {

        UNEQUAL("!="),

        EQUAL("=="),

        GREATER(">"),

        LESS("<");

        String flag;

        Operator(String flag) {
            this.flag = flag;
        }

        public String getFlag() {
            return flag;
        }
    }

    String[] factors;
    Operator operator;

    public Operation(String[] factors, Operator operator) {
        this.factors = factors;
        this.operator = operator;
    }

    boolean calc() {

        switch (operator) {
            case GREATER:
                return factors[0].compareTo(factors[1]) > 0;
            case LESS:
                return factors[0].compareTo(factors[1]) < 0;
            case EQUAL:
                return factors[0].compareTo(factors[1]) == 0;
            case UNEQUAL:
                return factors[0].compareTo(factors[1]) != 0;
            default:
                return false;
        }
    }
}
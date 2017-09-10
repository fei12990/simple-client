package pers.luofei.http.client.codec;

import pers.luofei.http.client.core.ProxyMethod;
import pers.luofei.http.client.core.SimpleHttpContext;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by luofei on 2017/9/8.
 */
public class FormDataEncoder implements RequestBodyEncoder {

    private static final String DEFAULT_BOUNDRAY = ProxyMethod.DEFAULT_BOUNDRAY;

    private static final String BOUNDRAY_FLAG = "--";

    private static final String DEFAULT_CONTENT_BOUNDRAY = BOUNDRAY_FLAG + DEFAULT_BOUNDRAY;

    private static final String NEWLINE = "\r\n";

    private static final String DEFAULT_DATA_CONTENT_TYPE = "\"Content-Type:application/octet-stream\"";

    private static byte[] BOUNDRAY_BYTES_LAST;

    private static byte[] NEWLINE_BYTES;

    static {
        BOUNDRAY_BYTES_LAST = (NEWLINE + DEFAULT_CONTENT_BOUNDRAY + BOUNDRAY_FLAG + NEWLINE).getBytes();
        NEWLINE_BYTES = NEWLINE.getBytes();
    }

    @Override
    public List<? extends InputStream> encode(Map<String, Object> requestParameters) throws CodecException {

        List<InputStream> result = new ArrayList<>();
        result.add(new ByteArrayInputStream(NEWLINE_BYTES));
        for (Map.Entry<String, Object> entry : requestParameters.entrySet()) {
            Object v = entry.getValue();
            if (v instanceof InputStream) {
                continue;
            }
            if (v instanceof File) {
                StringBuilder tmp = new StringBuilder(DEFAULT_CONTENT_BOUNDRAY);
                tmp.append(NEWLINE);
                tmp.append("Content-Disposition: form-data; name=\"");
                tmp.append(entry.getKey());
                tmp.append("\"; filename=\"");
                tmp.append(((File) v).getName());
                tmp.append("\"");
                tmp.append(NEWLINE);
                tmp.append(DEFAULT_DATA_CONTENT_TYPE);
                tmp.append(NEWLINE);
                tmp.append(NEWLINE);
                try {
                    result.add(new ByteArrayInputStream(tmp.toString().getBytes(SimpleHttpContext.DEFAULT_CHARSET)));
                    result.add(new FileInputStream((File) v));
                } catch (Exception e) {
                    throw new CodecException(e);
                }
                result.add(new ByteArrayInputStream(NEWLINE_BYTES));
            } else {
                StringBuilder tmp = new StringBuilder(DEFAULT_CONTENT_BOUNDRAY);
                tmp.append(NEWLINE);
                tmp.append("Content-Disposition: form-data; name=\"");
                tmp.append(entry.getKey());
                tmp.append("\"");
                tmp.append(NEWLINE);
                tmp.append(NEWLINE);
                try {
                    result.add(new ByteArrayInputStream(tmp.toString().getBytes(SimpleHttpContext.DEFAULT_CHARSET)));
                    result.add(new ByteArrayInputStream(entry.getValue().toString().getBytes(SimpleHttpContext.DEFAULT_CHARSET)));
                } catch (UnsupportedEncodingException e) {
                    throw new CodecException(e);
                }
                result.add(new ByteArrayInputStream(NEWLINE_BYTES));
            }
        }
        if (!requestParameters.isEmpty()) {
            result.add(result.size(), new ByteArrayInputStream(BOUNDRAY_BYTES_LAST));
        }
        return result;
    }
}

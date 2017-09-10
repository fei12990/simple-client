package pers.luofei.http.client.core;

/**
 * Created by luofei on 2017/9/6.
 */
public class DefaultCallback implements ResponseCallback {

    public static final DefaultCallback DEFAULT_CALLBACK = new DefaultCallback();

    @Override
    public void httpFailed(Object result, int code) {

        System.out.println("HTTP_CODE = " + code);
        System.out.println("result:\n" + result);
    }

    @Override
    public void success(Object result) {

        System.out.println(result);
    }

    @Override
    public void failed(Object result) {

        System.out.println(result);
    }

    @Override
    public void onProcess(long total, long transferred) {

    }
}

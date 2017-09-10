package pers.luofei.http.client.core;

import pers.luofei.http.client.SimpleHttpException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by luofei on 2017/8/31.
 */
public class SimpleHttpProcessor implements InvocationHandler {

    private ProxyMethod proxyMethod;

    public SimpleHttpProcessor() {
        this.proxyMethod = new ProxyMethod();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (Object.class.equals(method.getDeclaringClass())) {
            try {
                return method.invoke(this, args);
            } catch (Throwable t) {
                // ignore
            }
        }

        RequestMetaInfo metaInfo;
        metaInfo = RequestMetaInfoCache.get(method.getDeclaringClass());

        if (metaInfo == null) {
            throw new SimpleHttpException(method.getDeclaringClass().getName() + " request meta info not found.");
        }
        return proxyMethod.execute(metaInfo, method, args);
    }
}

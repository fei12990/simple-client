package pers.luofei.http.client.services;

import pers.luofei.http.client.core.RequestMetaInfoCache;
import pers.luofei.http.client.core.SimpleHttpProcessor;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by luofei on 2017/8/31.
 */
@SuppressWarnings("unchecked")
public class ServiceManager {

    private static ServiceManager manager;

    private Map<Class<?>, Object> services;

    private SimpleHttpProcessor simpleHttpProcessor;

    private ServiceManager() {

        this.services = new HashMap<>();
        simpleHttpProcessor = new SimpleHttpProcessor();
    }

    public static ServiceManager getManager() {

        if (manager == null) {
            manager = new ServiceManager();
        }
        return manager;
    }

	public <T> T getService(Class<T> clazz) {

        Object service = services.get(clazz);
        if (service == null) {
            service = newInstance(clazz);
        }
        return (T) service;
    }

    public <T> T getService(Class<T> clazz, String host) {

        Object service = services.get(clazz);
        if (service == null) {
            service = newInstance(clazz);
        }
        if (service != null) {
            RequestMetaInfoCache.updateHost(clazz, host);
        }
        return (T) service;
    }

    private <T> T newInstance(Class<T> clazz) {

        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, simpleHttpProcessor);
    }
}

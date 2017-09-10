package pers.luofei.http.client.annotations;

import pers.luofei.http.client.core.NullRequestHostDiscovery;
import pers.luofei.http.client.core.RequestHostDiscovery;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by luofei on 2017/9/4.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequestHost {

    String value() default "";

    /**
     * 当前请求的负载均衡标识<br>
     * 当服务使用了负载均衡时，可将服务的标识写到该注解中，
     * 再通过实现{@link RequestHostDiscovery}接口，来找到具体的请求地址。
     *
     * @return
     */
    String balanceId() default "";

    Class<? extends RequestHostDiscovery> hostDiscovery() default NullRequestHostDiscovery.class;

}


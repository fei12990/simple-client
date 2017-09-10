package pers.luofei.http.client.annotations;

import pers.luofei.http.client.codec.ResponseBodyDecoder;
import pers.luofei.http.client.core.RequestMethodInfo;

import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记请求方法<br/>
 * 当使用自定义{@link ResponseBodyDecoder}时，可以根据{@link RequestMethodInfo#id}来判断当前请求，再决定如果decode返回数据
 *
 * @see ResponseBodyDecoder#decode(RequestMethodInfo, int, InputStream)
 * @author luofei on 2017/9/6.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestId {

    String value() default "";
}

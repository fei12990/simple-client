package pers.luofei.http.client.annotations;

import pers.luofei.http.client.core.HttpProtocol;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by luofei on 2017/8/31.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequestProtocol {

    HttpProtocol value() default HttpProtocol.HTTP;
}

package pers.luofei.http.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by luofei on 2017/8/31.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleHttp {

    String NAME = "SIMPLE_HTTP_CLIENT";

    String VER = "0.0.1";
}

package pers.luofei.http.client.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by luofei on 2017/8/31.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestHeaders {

    String value() default "";
}

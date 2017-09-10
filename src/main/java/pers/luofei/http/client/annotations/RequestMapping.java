package pers.luofei.http.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by luofei on 2017/9/4.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    String GET = "GET";

    String POST = "POST";

    String PUT = "PUT";

    String DELETE = "DELETE";

    String value() default "";

    String method() default GET;
}

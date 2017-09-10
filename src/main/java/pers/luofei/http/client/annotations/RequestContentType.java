package pers.luofei.http.client.annotations;

import pers.luofei.http.client.core.ContentType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by luofei on 2017/9/4.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestContentType {

    ContentType value() default ContentType.FORM_URLENCODED;
}

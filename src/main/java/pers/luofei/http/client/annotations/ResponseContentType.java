package pers.luofei.http.client.annotations;

import pers.luofei.http.client.codec.DefaultResponseBodyDecoder;
import pers.luofei.http.client.codec.ResponseBodyDecoder;
import pers.luofei.http.client.core.ContentType;
import pers.luofei.http.client.core.DefaultCallback;
import pers.luofei.http.client.core.RequestMethodInfo;
import pers.luofei.http.client.core.ResponseCallback;

import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by luofei on 2017/9/4.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseContentType {

    ContentType value() default ContentType.JSON;

    /**
     * 返回数据中，实际要使用到的节点<br/>
     * 在服务端返回的数据中，可能包含了额外的节点信息，
     * 而我们用到的只是其中的一部分，此时可以使用该值来指定返回的数据的哪个节点是我们需要的。
     * 示例：
     * 服务端返回:
     * {
     *     code:0,
     *     data:{
     *         name:"http",
     *         value:"1.1"
     *     }
     * }
     * 我们的接口方法的返回值只需要value部分的数据，那么只需要将node="/data/value"即可。<br/>
     * 另外，如果业务上返回失败，可以参见{@link ResponseContentType#condition()}和{@link ResponseContentType#callback()}
     *
     * @return 真实节点部分
     */
    String node() default "";

    /**
     * 回调类的Class，默认为{@link DefaultCallback}
     * 可以自定义回调函数，需要实现{@link ResponseCallback}接口
     *
     * @return 回调类的Class
     */
    Class<? extends ResponseCallback> callback() default DefaultCallback.class;

    /**
     * 结果成功或失败的校验条件,用于返回数据的业务层面的校验。<br/>
     * 示例：
     * <p>
     * condition={"code=0"}，
     * 在返回的json数据如：<br/>
     * {<br/>
     * code:0,<br/>
     * date:"abc"<br/>
     * }<br/>
     * 则会触发{@link ResponseCallback#success(Object)}<br/>
     * 否则触发{@link ResponseCallback#failed(Object)}<br/>
     * 该值用于{@link DefaultResponseBodyDecoder}中，如果开发者需要自定义{@link ResponseBodyDecoder},
     * 可参照DefaultResponseBodyDecoder中的实现，或者直接继承DefaultResponseBodyDecoder，
     * 重写{@link DefaultResponseBodyDecoder#decode(RequestMethodInfo, InputStream)}方法<br/>
     * {@link DefaultResponseBodyDecoder}实现的操作符有:>,<,==,!=
     *
     * @see DefaultResponseBodyDecoder
     * @return 条件数组
     */
    String[] condition() default {};
}

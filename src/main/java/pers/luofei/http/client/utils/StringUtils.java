package pers.luofei.http.client.utils;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by luofei on 2017/9/6.
 */
public class StringUtils {

    public static boolean isEmpty(String str) {

        return str == null || str.isEmpty();
    }

    public static boolean isEmpty(String... str) {

        for (String s : str) {
            if (isEmpty(s)) {
                return true;
            }
        }
        return false;
    }

    public static String join(Collection<?> collection){
        return join(collection,",");
    }

    public static String join(Collection<?> collection,String separator){
        Iterator<?> iterator = collection.iterator();
        if(!(collection.size() > 0 && iterator.hasNext()))
            return "";
        StringBuilder sb = new StringBuilder();
        sb.append(iterator.next());
        while (iterator.hasNext()){
            sb.append(separator);
            sb.append(iterator.next());
        }
        return sb.toString();
    }
}

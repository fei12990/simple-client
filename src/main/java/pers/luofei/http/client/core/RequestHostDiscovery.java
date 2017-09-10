package pers.luofei.http.client.core;

/**
 * Created by luofei on 2017/9/6.
 */
public interface RequestHostDiscovery {

    String getRequestHost(String balanceId);
}

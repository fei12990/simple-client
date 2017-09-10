package pers.luofei.http.client.core;

/**
 * Created by luofei on 2017/9/8.
 */
public class NullRequestHostDiscovery implements RequestHostDiscovery {
    @Override
    public String getRequestHost(String balanceId) {
        return null;
    }
}

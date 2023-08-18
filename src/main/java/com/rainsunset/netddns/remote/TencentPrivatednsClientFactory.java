package com.rainsunset.netddns.remote;

import com.tencentcloudapi.common.Credential;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TencentPrivatednsClientFactory {
    private static Map<String, TencentPrivatednsClient> aliClientMap;

    public static TencentPrivatednsClient getTencentClient(String secretId, String secretKey, String region) {
        String key = secretId + secretKey;
        if (null == aliClientMap) {
            aliClientMap = new HashMap<>();
        }
        TencentPrivatednsClient client = aliClientMap.get(key);
        if (null != client) {
            return client;
        }
        Credential credential = new Credential(secretId, secretKey);
        client = new TencentPrivatednsClient(credential, region);
        aliClientMap.put(key, client);
        return client;
    }
}

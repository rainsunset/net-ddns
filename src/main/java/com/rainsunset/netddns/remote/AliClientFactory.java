package com.rainsunset.netddns.remote;

import com.aliyun.alidns20150109.Client;
import com.aliyun.teaopenapi.models.Config;
import com.google.common.base.Throwables;
import com.rainsunset.netddns.common.enums.ErrorCode;
import com.rainsunset.netddns.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AliClientFactory {
    private static Map<String, Client> aliClientMap;

    public static Client getAliClient(String accessId, String accessSecret) {
        String key = accessId + accessSecret;
        if (null == aliClientMap) {
            aliClientMap = new HashMap<>();
        }
        Client client = aliClientMap.get(key);
        if (null != client) {
            return client;
        }
        Config config = new Config();
        config.accessKeyId = accessId;
        config.accessKeySecret = accessSecret;
        try {
            client = new Client(config);
            aliClientMap.put(key, client);
        } catch (Exception e) {
            log.error("getAliClient Error >> createClient fail[errorMsg:{}]", Throwables.getStackTraceAsString(e));
        }
        return client;
    }
}

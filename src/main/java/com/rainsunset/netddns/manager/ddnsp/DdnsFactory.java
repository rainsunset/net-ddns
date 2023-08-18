package com.rainsunset.netddns.manager.ddnsp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DdnsFactory {

    @Autowired
    private Map<String, Ddns> packageMap;

    public Ddns creatDdns(String type) {
        return packageMap.get(type);
    }
}

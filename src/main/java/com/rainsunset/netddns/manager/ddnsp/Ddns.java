package com.rainsunset.netddns.manager.ddnsp;

import com.rainsunset.netddns.service.request.TodoDdnsBO;

public interface Ddns {
    public boolean ddns(String ip, TodoDdnsBO platformBO);
}

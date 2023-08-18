package com.rainsunset.netddns.remote;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import com.google.common.base.Throwables;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JsonIpRemote {

    public String getIp() {
        String body = "";
        try {
            body = HttpRequest.get("https://jsonip.com").execute().body();
        } catch (HttpException e) {
            log.error("getIp error > http request fail [errorMsg:{}]", Throwables.getStackTraceAsString(e));
            return "";
        }
        try {
            return JsonParser.parseString(body).getAsJsonObject().get("ip").getAsString();
        } catch (JsonSyntaxException e) {
            log.error("getIp error > ip analysis fail [errorMsg:{}]", Throwables.getStackTraceAsString(e));
            return "";
        }
    }

}

package com.rainsunset.netddns.service.request;

import lombok.Data;

import java.util.List;

@Data
public class TodoDdnsBO {
    /** 域名运营商平台 {@link com.rainsunset.netddns.common.enums.PlatformEnum}*/
    private String platform;
    private String accessId;
    private String accessSecret;
    /** 待解析的域名列表 */
    private List<DomainBO> domainList;
}

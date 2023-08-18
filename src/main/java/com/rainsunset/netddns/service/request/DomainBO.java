package com.rainsunset.netddns.service.request;

import lombok.Data;

@Data
public class DomainBO {
    /** 域名: 不能为空 */
    private String domainName;
    /** 解析路线 为空时默认为默认为默认解析路线 */
    private String line;
    /** 子域名：子域名不为空时将忽略recoreType配置 */
    private String[] subDomains;
}

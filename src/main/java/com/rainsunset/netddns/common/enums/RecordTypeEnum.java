package com.rainsunset.netddns.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 记录类型枚举
 */
@Getter
@AllArgsConstructor
public enum RecordTypeEnum {

    A("A","A记录"),
    // tencent: "A", "CNAME", "MX", "TXT", "NS", "SPF", "SRV", "CAA", "显性URL", "隐性URL"
    ;

    /**
     * 默认Code，适用于aliyun、tencentcloud
     */
    private String code;
    private String desc;
}

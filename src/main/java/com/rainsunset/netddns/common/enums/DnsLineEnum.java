package com.rainsunset.netddns.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DNS解析线路枚举
 */
@Getter
@AllArgsConstructor
public enum DnsLineEnum {
    DEFAULT("default","默认","默认"),
    TELECOM("telecom","telecom","电信"),
    UNICOM("unicom","unicom","联通"),
    MOBILE("mobile","mobile","移动"),
    OVERSEA("oversea","oversea","海外"),
    EDU("edu","edu","教育网"),
    DRPENG("drpeng","drpeng","鹏博士"),
    BTVN("btvn","btvn","广电网"),
    ;
    /** 默认Code，适用于aliyun */
    private String code;
    private String tencentCode;
    private String desc;
}

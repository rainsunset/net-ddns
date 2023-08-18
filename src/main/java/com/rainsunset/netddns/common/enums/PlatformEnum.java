package com.rainsunset.netddns.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 支持的云供应商枚举 */
@Getter
@AllArgsConstructor
public enum PlatformEnum {

    ALI("aliyun", "ali云"),
    TENCENT("tencentcloud", "腾讯云"),
    ;
    private String code;
    private String desc;
}

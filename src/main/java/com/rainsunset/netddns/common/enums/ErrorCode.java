package com.rainsunset.netddns.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Childe on 2018/10/10.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     200，成功
     201，已存在
     401，未授权
     403，禁止
     404，不存在
     405，状态异常
     412，前提条件失败
     415，格式错
     500，内部错误
     */

    //201 已存在
    //201 已存在

    //403

    //404 不存在

    // 412,前提失败

    // region412000 - 412099 调用第三方服务异常
    ERROR_CODE_412000("APPEC_412000", "RPC调用异常"),
    //endregion

    // region412100 - 412199 aliyun 相关异常
    APPEC_412100("APPEC_412100", "创建aliyun连接失败"),
    // endregion

    // region412200 - 412299 腾讯云 相关异常
    APPEC_412200("APPEC_412200", "创建连接失败"),
    APPEC_412201("APPEC_412201", "获取私有域列表异常"),
    APPEC_412202("APPEC_412202", "获取私有域记录列表异常"),
    APPEC_412203("APPEC_412203", "修改私有域解析记录异常"),
    // endregion

    //415 格式错

    //500 内部错误
    APPEC_500001("APPEC_500003", "系统忙，请稍后再试"),
    ;



    private String code;

    private String desc;

    /**
     * 通过错误码code获取错误描述desc
     * @param code
     * @return
     */
    public static String getDescByCode(String code) {
        for (ErrorCode enums : ErrorCode.values()) {
            if (enums.getCode().equals(code)) {
                return enums.getDesc();
            }
        }
        return code + "未定义";
    }
}

package com.rainsunset.netddns.remote;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.rainsunset.netddns.remote.model.tencentdnspod.DescribeRecordListRequest;
import com.rainsunset.netddns.remote.model.tencentdnspod.DescribeRecordListResponse;
import com.rainsunset.netddns.remote.model.tencentdnspod.ModifyRecordRequest;
import com.rainsunset.netddns.remote.model.tencentdnspod.ModifyRecordResponse;
import com.tencentcloudapi.common.AbstractClient;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.JsonResponseModel;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;

import java.lang.reflect.Type;

// https://cloud.tencent.com/document/api/1427/56166
// https://github.com/TencentCloud/tencentcloud-sdk-java/blob/master/src/main/java/com/tencentcloudapi/dnspod/v20210323/DnspodClient.java
public class TencentDnspodClient extends AbstractClient {
    private static String endpoint = "dnspod.tencentcloudapi.com";
    private static String service = "dnspod";
    private static String version = "2021-03-23";

    public TencentDnspodClient(Credential credential, String region) {
        this(credential, region, new ClientProfile());
    }

    public TencentDnspodClient(Credential credential, String region, ClientProfile profile) {
        super(TencentDnspodClient.endpoint, TencentDnspodClient.version, credential, region, profile);
    }

    /**
     *获取某个域名下的解析记录列表
     * https://cloud.tencent.com/document/api/1427/56166
     * @param req DescribeRecordListRequest
     * @return DescribeRecordListResponse
     * @throws TencentCloudSDKException
     */
    public DescribeRecordListResponse describeRecordList(DescribeRecordListRequest req) throws TencentCloudSDKException {
        JsonResponseModel<DescribeRecordListResponse> rsp = null;
        String rspStr = "";
        req.setSkipSign(false);
        try {
            Type type = new TypeToken<JsonResponseModel<DescribeRecordListResponse>>() {
            }.getType();
            rspStr = this.internalRequest(req, "DescribeRecordList");
            rsp  = gson.fromJson(rspStr, type);
        } catch (JsonSyntaxException e) {
            throw new TencentCloudSDKException("response message: " + rspStr + ".\n Error message: " + e.getMessage());
        }
        return rsp.response;
    }

    /**
     *修改记录
     * https://cloud.tencent.com/document/api/1427/56157
     * @param req ModifyRecordRequest
     * @return ModifyRecordResponse
     * @throws TencentCloudSDKException
     */
    public ModifyRecordResponse modifyRecord(ModifyRecordRequest req) throws TencentCloudSDKException{
        JsonResponseModel<ModifyRecordResponse> rsp = null;
        String rspStr = "";
        req.setSkipSign(false);
        try {
            Type type = new TypeToken<JsonResponseModel<ModifyRecordResponse>>() {
            }.getType();
            rspStr = this.internalRequest(req, "ModifyRecord");
            rsp  = gson.fromJson(rspStr, type);
        } catch (JsonSyntaxException e) {
            throw new TencentCloudSDKException("response message: " + rspStr + ".\n Error message: " + e.getMessage());
        }
        return rsp.response;
    }
}

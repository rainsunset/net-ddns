package com.rainsunset.netddns.remote;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.rainsunset.netddns.remote.model.tencentprivatedns.*;
import com.tencentcloudapi.common.AbstractClient;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.JsonResponseModel;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;

import java.lang.reflect.Type;

// https://cloud.tencent.com/document/product/1338
// https://github.com/TencentCloud/tencentcloud-sdk-java/blob/master/src/main/java/com/tencentcloudapi/privatedns/v20201028/PrivatednsClient.java
public class TencentPrivatednsClient extends AbstractClient {
    private static String endpoint = "privatedns.tencentcloudapi.com";
    private static String service = "privatedns";
    private static String version = "2020-10-28";

    public TencentPrivatednsClient(Credential credential, String region) {
        this(credential, region, new ClientProfile());
    }

    public TencentPrivatednsClient(Credential credential, String region, ClientProfile profile) {
        super(TencentPrivatednsClient.endpoint, TencentPrivatednsClient.version, credential, region, profile);
    }

    /**
     * 获取私有域列表
     * https://cloud.tencent.com/document/product/1338/55939
     * @param req DescribePrivateZoneListRequest
     * @return DescribePrivateZoneListResponse
     * @throws TencentCloudSDKException
     */
    public DescribePrivateZoneListResponse describePrivateZoneList(DescribePrivateZoneListRequest req) throws TencentCloudSDKException{
        JsonResponseModel<DescribePrivateZoneListResponse> rsp = null;
        String rspStr = "";
        req.setSkipSign(false);
        try {
            Type type = new TypeToken<JsonResponseModel<DescribePrivateZoneListResponse>>() {
            }.getType();
            rspStr = this.internalRequest(req, "DescribePrivateZoneList");
            rsp  = gson.fromJson(rspStr, type);
        } catch (JsonSyntaxException e) {
            throw new TencentCloudSDKException("response message: " + rspStr + ".\n Error message: " + e.getMessage());
        }
        return rsp.response;
    }

    /**
     * 获取私有域记录列表
     * https://cloud.tencent.com/document/product/1338/55938
     * @param req DescribePrivateZoneRecordListRequest
     * @return DescribePrivateZoneRecordListResponse
     * @throws TencentCloudSDKException
     */
    public DescribePrivateZoneRecordListResponse describePrivateZoneRecordList(DescribePrivateZoneRecordListRequest req) throws TencentCloudSDKException{
        JsonResponseModel<DescribePrivateZoneRecordListResponse> rsp = null;
        String rspStr = "";
        req.setSkipSign(false);
        try {
            Type type = new TypeToken<JsonResponseModel<DescribePrivateZoneRecordListResponse>>() {
            }.getType();
            rspStr = this.internalRequest(req, "DescribePrivateZoneRecordList");
            rsp  = gson.fromJson(rspStr, type);
        } catch (JsonSyntaxException e) {
            throw new TencentCloudSDKException("response message: " + rspStr + ".\n Error message: " + e.getMessage());
        }
        return rsp.response;
    }

    /**
     * 修改私有域解析记录
     * https://cloud.tencent.com/document/product/1338/55934
     * @param req ModifyPrivateZoneRecordRequest
     * @return ModifyPrivateZoneRecordResponse
     * @throws TencentCloudSDKException
     */
    public ModifyPrivateZoneRecordResponse modifyPrivateZoneRecord(ModifyPrivateZoneRecordRequest req) throws TencentCloudSDKException{
        JsonResponseModel<ModifyPrivateZoneRecordResponse> rsp = null;
        String rspStr = "";
        req.setSkipSign(false);
        try {
            Type type = new TypeToken<JsonResponseModel<ModifyPrivateZoneRecordResponse>>() {
            }.getType();
            rspStr = this.internalRequest(req, "ModifyPrivateZoneRecord");
            rsp  = gson.fromJson(rspStr, type);
        } catch (JsonSyntaxException e) {
            throw new TencentCloudSDKException("response message: " + rspStr + ".\n Error message: " + e.getMessage());
        }
        return rsp.response;
    }
}

package com.rainsunset.netddns.manager.ddnsp;

import com.aliyun.teautil.Common;
import com.google.common.base.Throwables;
import com.rainsunset.netddns.common.enums.DnsLineEnum;
import com.rainsunset.netddns.common.enums.ErrorCode;
import com.rainsunset.netddns.common.enums.RecordTypeEnum;
import com.rainsunset.netddns.common.exception.ServiceException;
import com.rainsunset.netddns.remote.TencentPrivatednsClient;
import com.rainsunset.netddns.remote.TencentPrivatednsClientFactory;
import com.rainsunset.netddns.remote.model.tencentprivatedns.DescribePrivateZoneListRequest;
import com.rainsunset.netddns.remote.model.tencentprivatedns.DescribePrivateZoneListResponse;
import com.rainsunset.netddns.remote.model.tencentprivatedns.DescribePrivateZoneRecordListRequest;
import com.rainsunset.netddns.remote.model.tencentprivatedns.DescribePrivateZoneRecordListResponse;
import com.rainsunset.netddns.remote.model.tencentprivatedns.Filter;
import com.rainsunset.netddns.remote.model.tencentprivatedns.ModifyPrivateZoneRecordRequest;
import com.rainsunset.netddns.remote.model.tencentprivatedns.ModifyPrivateZoneRecordResponse;
import com.rainsunset.netddns.remote.model.tencentprivatedns.PrivateZone;
import com.rainsunset.netddns.remote.model.tencentprivatedns.PrivateZoneRecord;
import com.rainsunset.netddns.service.request.DomainBO;
import com.rainsunset.netddns.service.request.TodoDdnsBO;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component("tencentcloudPrivate")
public class TencentCloudPrivateDdns implements Ddns {

    @Value("${remote.tencent-cloud.region}")
    private String region;

    @Override
    public boolean ddns(String ip, TodoDdnsBO platformBO) {

        TencentPrivatednsClient domainClient = TencentPrivatednsClientFactory.getTencentClient(
                platformBO.getAccessId(), platformBO.getAccessSecret(), region);
        if (null == domainClient) {
            throw new ServiceException(ErrorCode.APPEC_412200);
        }

        DescribePrivateZoneListResponse zoneListRsp;
        try {
            zoneListRsp = domainClient.describePrivateZoneList(new DescribePrivateZoneListRequest());
        } catch (TencentCloudSDKException e) {
            log.error("tencentPrivateDdns >> describePrivateZoneList Error[errorMsg:{}]", Throwables.getStackTraceAsString(e));
            throw new ServiceException(ErrorCode.APPEC_412201);
        }
        if (null == zoneListRsp || null == zoneListRsp.getPrivateZoneSet() || zoneListRsp.getPrivateZoneSet().length < 1) {
            log.error("tencentPrivateDdns >> describePrivateZoneList Is Empty[zoneListRsp:{}]", Common.toJSONString(zoneListRsp));
            return false;
        }
        // 映射：Map<域名,域名信息>
        Map<String, PrivateZone> domainNameZoneMap = Arrays.stream(zoneListRsp.getPrivateZoneSet())
                .collect(Collectors.toMap(PrivateZone::getDomain, obj -> obj));
        final boolean[] res = {true};
        List<DomainBO> domainList = platformBO.getDomainList();
        domainList.stream().forEach(domainBO -> {
            String domainName = domainBO.getDomainName();
            PrivateZone privateZone = domainNameZoneMap.get(domainName);
            if (null == privateZone) {
                log.error("tencentPrivateDdns >> domainName Is No Fund[domainName:{}]", domainName);
                res[0] = false;
                return;
            }
            String zoneId = privateZone.getZoneId();

            // 获取私有域记录列表
            DescribePrivateZoneRecordListRequest zoneRecordListReq = new DescribePrivateZoneRecordListRequest();
            zoneRecordListReq.setZoneId(zoneId);
            // 过滤RecordType
            Filter recordTypeFilter = new Filter();
            recordTypeFilter.setName("RecordType");
            recordTypeFilter.setValues(new String[]{RecordTypeEnum.A.getCode()});
            zoneRecordListReq.setFilters(new Filter[]{recordTypeFilter});
            DescribePrivateZoneRecordListResponse zoneRecordListRsp;
            try {
                zoneRecordListRsp = domainClient.describePrivateZoneRecordList(zoneRecordListReq);
            } catch (TencentCloudSDKException e) {
                log.error("tencentPrivateDdns >> describePrivateZoneRecordList Error[zoneRecordListReq:{}, errorMsg:{}]",
                        Common.toJSONString(zoneRecordListReq), Throwables.getStackTraceAsString(e));
                res[0] = false;
                return;
            }
            if (null == zoneRecordListRsp || null == zoneRecordListRsp.getRecordSet() || zoneRecordListRsp.getRecordSet().length < 1) {
                log.error("tencentPrivateDdns >> describePrivateZoneRecordList ResEmpty[zoneRecordListReq:{},zoneRecordListRsp:{}]",
                        Common.toJSONString(zoneRecordListReq),Common.toJSONString(zoneRecordListRsp));
                res[0] = false;
                return;
            }
            List<PrivateZoneRecord> records = Arrays.asList(zoneRecordListRsp.getRecordSet());
            // 过滤目标子域名
            String[] subDomains = domainBO.getSubDomains();
            if (null != subDomains && !CollectionUtils.isEmpty(Arrays.asList(subDomains))) {
                records = records.stream().filter(obj -> Arrays.asList(subDomains).contains(obj.getSubDomain()))
                        .collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(records)) {
                log.info("tencentPrivateDdns >> no records need change[subDomains:{}, zoneRecordListRsp:{}]",
                        Common.toJSONString(subDomains), Common.toJSONString(zoneRecordListRsp));
                res[0] = false;
                return;
            }
            int count = 0;
            for (PrivateZoneRecord domainRecord : records) {
                // {"RecordId":"66","ZoneId":"zone-123456","SubDomain":"b","RecordType":"A","RecordValue":"3.3.3.3","TTL":600,"MX":0,"Status":"enabled","Extra":"weight:100","CreatedOn":"2020-11-16 17:16:24","UpdatedOn":"2020-11-16 17:16:24","Weight":100,"Enabled":0}
                // 逐个检查域名解析记录是否指向当前IP
                if (ip.equals(domainRecord.getRecordValue())) {
                    // 解析目标为当前IP 不执行操作
                    count++;
                    continue;
                }
                // 修改域名解析记录
                ModifyPrivateZoneRecordRequest modifyZoneRecordReq = new ModifyPrivateZoneRecordRequest();
                modifyZoneRecordReq.setZoneId(zoneId);
                modifyZoneRecordReq.setRecordId(domainRecord.getRecordId());
                modifyZoneRecordReq.setRecordType(domainRecord.getRecordType());
                modifyZoneRecordReq.setSubDomain(domainRecord.getSubDomain());
                modifyZoneRecordReq.setRecordValue(ip);
                ModifyPrivateZoneRecordResponse modifyZoneRecordResp;
                try {
                    modifyZoneRecordResp = domainClient.modifyPrivateZoneRecord(modifyZoneRecordReq);
                } catch (TencentCloudSDKException e) {
                    log.error("tencentPrivateDdns >> modifyPrivateZoneRecord Error[modifyZoneRecordReq:{}, errorMsg:{}]",
                            Common.toJSONString(modifyZoneRecordReq), Throwables.getStackTraceAsString(e));
                    res[0] = false;
                    continue;
                }
                count ++;
                log.info("tencentPrivateDdns >> modifyPrivateZoneRecord success[RES:{}]", Common.toJSONString(modifyZoneRecordResp));
            }
            log.info("tencentPrivateDdns updateDomain success[domainBO:{}, count:{}]", Common.toJSONString(domainBO), count);
        });
        return res[0];
    }
}

package com.rainsunset.netddns.manager.ddnsp;

import com.aliyun.teautil.Common;
import com.google.common.base.Throwables;
import com.rainsunset.netddns.common.enums.DnsLineEnum;
import com.rainsunset.netddns.common.enums.ErrorCode;
import com.rainsunset.netddns.common.enums.RecordTypeEnum;
import com.rainsunset.netddns.common.exception.ServiceException;
import com.rainsunset.netddns.remote.TencentDnsPodClientFactory;
import com.rainsunset.netddns.remote.TencentDnspodClient;
import com.rainsunset.netddns.remote.TencentPrivatednsClient;
import com.rainsunset.netddns.remote.TencentPrivatednsClientFactory;
import com.rainsunset.netddns.remote.model.tencentdnspod.DescribeRecordListRequest;
import com.rainsunset.netddns.remote.model.tencentdnspod.DescribeRecordListResponse;
import com.rainsunset.netddns.remote.model.tencentdnspod.ModifyRecordRequest;
import com.rainsunset.netddns.remote.model.tencentdnspod.ModifyRecordResponse;
import com.rainsunset.netddns.remote.model.tencentdnspod.RecordListItem;
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
@Component("tencentcloud")
public class TencentCloudDdns implements Ddns {

    @Value("${remote.tencent-cloud.region}")
    private String region;

    @Override
    public boolean ddns(String ip, TodoDdnsBO platformBO) {
        TencentDnspodClient dnspodClient = TencentDnsPodClientFactory.getTencentClient(
                platformBO.getAccessId(), platformBO.getAccessSecret(), region);
        if (null == dnspodClient) {
            throw new ServiceException(ErrorCode.APPEC_412200);
        }
        final boolean[] res = {true};
        List<DomainBO> domainList = platformBO.getDomainList();
        domainList.stream().forEach(domainBO -> {
            String line = StringUtils.hasText(domainBO.getLine()) ? domainBO.getLine() : DnsLineEnum.DEFAULT.getTencentCode();
            // 获取某个域名下的解析记录列表
            DescribeRecordListRequest recordListReq = new DescribeRecordListRequest();
            String domainName = domainBO.getDomainName();
            recordListReq.setDomain(domainName);
            recordListReq.setRecordLine(line);
            recordListReq.setRecordType(RecordTypeEnum.A.getCode());
            DescribeRecordListResponse recordListRes;
            try {
                recordListRes = dnspodClient.describeRecordList(recordListReq);
            } catch (TencentCloudSDKException e) {
                log.error("tencentDdns >> describeRecordList Error[recordListReq:{}, errorMsg:{}]",
                        Common.toJSONString(recordListReq), Throwables.getStackTraceAsString(e));
                res[0] = false;
                return;
            }
            if (null == recordListRes || null == recordListRes.getRecordList() || recordListRes.getRecordList().length < 1) {
                log.error("tencentDdns >> describeRecordList ResEmpty [recordListReq:{}, recordListRes:{}]",
                        Common.toJSONString(recordListReq), Common.toJSONString(recordListRes));
                res[0] = false;
                return;
            }

            // 过滤目标子域名
            List<RecordListItem> records = Arrays.asList(recordListRes.getRecordList());
            String[] subDomains = domainBO.getSubDomains();
            if (null != subDomains && !CollectionUtils.isEmpty(Arrays.asList(subDomains))) {
                records = records.stream().filter(obj -> Arrays.asList(subDomains).contains(obj.getName()))
                        .collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(records)) {
                log.info("tencentDdns >> no records need change[subDomains:{},recordListRes:{}]",
                        Common.toJSONString(subDomains), Common.toJSONString(recordListRes));
                res[0] = false;
                return;
            }
            int count = 0;
            for (RecordListItem domainRecord : records) {
                // {"RecordId":556507778,"Value":"f1g1ns1.dnspod.net.","Status":"ENABLE","UpdatedOn":"2021-03-28 11:27:09","Name":"@","Line":"默认","LineId":"0","Type":"NS","Weight":null,"MonitorStatus":"","Remark":"","TTL":86400,"MX":0}
                // 逐个检查域名解析记录是否指向当前IP
                if (ip.equals(domainRecord.getValue())) {
                    // 解析目标为当前IP 不执行操作
                    count++;
                    continue;
                }
                // 修改域名解析记录
                ModifyRecordRequest modifyRecordReq = new ModifyRecordRequest();
                modifyRecordReq.setDomain(domainName);
                modifyRecordReq.setRecordType(domainRecord.getType());
                modifyRecordReq.setRecordLine(domainRecord.getLine());
                modifyRecordReq.setValue(ip);
                modifyRecordReq.setRecordId(domainRecord.getRecordId());
                modifyRecordReq.setSubDomain(domainRecord.getName());
                ModifyRecordResponse modifyRecordRes;
                try {
                    modifyRecordRes = dnspodClient.modifyRecord(modifyRecordReq);
                } catch (TencentCloudSDKException e) {
                    log.error("tencentDdns >> modifyRecord Error[modifyRecordReq:{}, errorMsg:{}]",
                            Common.toJSONString(modifyRecordReq), Throwables.getStackTraceAsString(e));
                    res[0] = false;
                    continue;
                }
                count ++;
                log.info("tencentDdns >> modifyRecord success[RES:{}]", Common.toJSONString(modifyRecordRes));
            }
            log.info("tencentDdns updateDomain success[domainBO:{}, count:{}]", Common.toJSONString(domainBO), count);
        });
        return res[0];
    }
}

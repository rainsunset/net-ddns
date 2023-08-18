package com.rainsunset.netddns.manager.ddnsp;

import com.aliyun.alidns20150109.Client;
import com.aliyun.alidns20150109.models.*;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.google.common.base.Throwables;
import com.rainsunset.netddns.common.enums.DnsLineEnum;
import com.rainsunset.netddns.common.enums.ErrorCode;
import com.rainsunset.netddns.common.enums.RecordTypeEnum;
import com.rainsunset.netddns.common.exception.ServiceException;
import com.rainsunset.netddns.remote.AliClientFactory;
import com.rainsunset.netddns.service.request.DomainBO;
import com.rainsunset.netddns.service.request.TodoDdnsBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component("aliyun")
public class AliyunDdns implements Ddns {

    @Override
    public boolean ddns(String ip, TodoDdnsBO platformBO) {
        Client domainClient = AliClientFactory.getAliClient(platformBO.getAccessId(), platformBO.getAccessSecret());
        if (null == domainClient) {
            throw new ServiceException(ErrorCode.APPEC_412100);
        }
        final boolean[] res = {true};
        List<DomainBO> domainList = platformBO.getDomainList();
        domainList.stream().forEach(domainBO -> {
            String domainName = domainBO.getDomainName();
            String line = StringUtils.hasText(domainBO.getLine()) ? domainBO.getLine() : DnsLineEnum.DEFAULT.getCode();

            // 获取解析记录列表
            DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest()
                    .setDomainName(domainName).setLine(line).setType(RecordTypeEnum.A.getCode());
            DescribeDomainRecordsResponse describeDomainRecordsResponse = null;
            try {
                describeDomainRecordsResponse = domainClient.describeDomainRecords(describeDomainRecordsRequest);
            } catch (Exception e) {
                log.error("aliDdns >> describeDomainRecords Error[request:{}, errorMsg:{}]",
                        Common.toJSONString(describeDomainRecordsRequest), Throwables.getStackTraceAsString(e));
                res[0] = false;
                return;
            }
            if (null == describeDomainRecordsResponse.body || null == describeDomainRecordsResponse.body.getDomainRecords()
                    || CollectionUtils.isEmpty(describeDomainRecordsResponse.body.getDomainRecords().getRecord())) {
                log.error("aliDdns >> describeDomainRecords ResEmpty[describeDomainRecordsRequest:{},describeDomainRecordsResponse:{}]",
                        Common.toJSONString(describeDomainRecordsRequest), Common.toJSONString(describeDomainRecordsResponse.body));
                res[0] = false;
                return;
            }
            // 过滤目标子域名
            List<DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord> records =
                    describeDomainRecordsResponse.body.getDomainRecords().getRecord();
            String[] subDomains = domainBO.getSubDomains();
            if (null != subDomains && !CollectionUtils.isEmpty(Arrays.asList(subDomains))) {
                records = records.stream().filter(obj -> Arrays.asList(subDomains).contains(obj.getRR())).collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(records)) {
                log.info("aliDdns >> no records need change[subDomains:{}, describeDomainRecordsResponse:{}]",
                        Common.toJSONString(subDomains), Common.toJSONString(describeDomainRecordsResponse.body));
                res[0] = false;
                return;
            }
            int count = 0;
            for (DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord domainRecord : records) {
                // {"domainName":"riches.fun","line":"default","locked":false,"RR":"jump","recordId":"782500655167158272","status":"ENABLE","TTL":600,"type":"A","value":"113.87.89.98","weight":1}
                // 逐个检查域名解析记录是否指向当前IP
                if (ip.equals(domainRecord.getValue())) {
                    // 解析目标为当前IP 不执行操作
                    count++;
                    continue;
                }
                // 修改域名解析记录
                UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest()
                        .setRecordId(domainRecord.getRecordId()).setRR(domainRecord.getRR()).setType(domainRecord.getType())
                        .setValue(ip).setTTL(domainRecord.getTTL()).setLine(domainRecord.getLine());
                UpdateDomainRecordResponse updateDomainRecordResponse = null;
                try {
                    updateDomainRecordResponse = domainClient.updateDomainRecord(updateDomainRecordRequest);
                } catch (Exception e) {
                    log.error("aliDdns >> updateDomainRecord Error[updateDomainRecordRequest:{}, errorMsg:{}]",
                            Common.toJSONString(updateDomainRecordRequest), Throwables.getStackTraceAsString(e));
                    res[0] = false;
                    continue;
                }
                count++;
                log.info("aliDdns >> updateDomainRecord success[RES:{}]", Common.toJSONString(updateDomainRecordResponse.body));
            }
            log.info("aliDdns updateDomain success[domainBO:{}, count:{}]", Common.toJSONString(domainBO), count);
        });
        return res[0];
    }
}

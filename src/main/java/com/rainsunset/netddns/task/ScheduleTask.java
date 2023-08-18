package com.rainsunset.netddns.task;

import com.google.common.base.Throwables;
import com.rainsunset.netddns.common.exception.ServiceException;
import com.rainsunset.netddns.config.TodoDdnsListConfig;
import com.rainsunset.netddns.manager.ddnsp.Ddns;
import com.rainsunset.netddns.manager.ddnsp.DdnsFactory;
import com.rainsunset.netddns.remote.JsonIpRemote;
import com.rainsunset.netddns.service.request.DomainBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableScheduling
public class ScheduleTask {

    private static String lastIp = "";

    @Autowired
    private JsonIpRemote jsonIpRemote;

    @Autowired
    private TodoDdnsListConfig todoDdnsListConfig;
    
    @Autowired
    private DdnsFactory ddnsFactory;

    @Scheduled(cron = "30 0/5 * * * ?")
    private void todoDdnsTask() {
        log.info("todoDdnsTask Start");
        if (null == todoDdnsListConfig || CollectionUtils.isEmpty(todoDdnsListConfig.getList())) {
            log.error("todoDdnsTask End ERROR > Todo Ddns List Empty");
            return;
        }
        String ip = jsonIpRemote.getIp();
        if ("".equals(ip)) {
            log.error("todoDdnsTask End ERROR > get Internet Ip Fail");
            return;
        }
        if (ip.equals(lastIp)) {
            // ip未发生改变 不执行变更操作
            log.info("todoDdnsTask End IP No Change[ip:{}]", ip);
            return;
        }
        final Boolean[] allSuccess = {true};
        todoDdnsListConfig.getList().stream().forEach(todoDdns -> {
            // 参数检查
            if (!StringUtils.hasText(todoDdns.getPlatform()) || CollectionUtils.isEmpty(todoDdns.getDomainList())) {
                log.error("todoDdnsTask ERROR > Illegal Todo Ddns Config [todoDdns:{}]", todoDdns);
                return;
            }
            String platform = todoDdns.getPlatform();
            Ddns ddns = ddnsFactory.creatDdns(platform);
            if (null == ddns) {
                log.error("todoDdnsTask ERROR > Not Support Platform[platform:{}]", platform);
                return;
            }
            List<String> domainList = todoDdns.getDomainList().stream().map(DomainBO::getDomainName).collect(Collectors.toList());
            try {
                boolean res = ddns.ddns(ip, todoDdns);
                log.info("todoDdnsTask Success [platform:{},domainList:{},res:{}]", platform, domainList, res);
                allSuccess[0] = allSuccess[0] && res;
            } catch (ServiceException e) {
                log.info("todoDdnsTask Fail > Business Exception [platform:{},domainList:{},errorMsg:{}]", platform, domainList, e.getMessage());
                allSuccess[0] = false;
            } catch (Exception e) {
                log.info("todoDdnsTask Fail > System Error [platform:{},domainList:{},errorMsg:{}]", platform, domainList, Throwables.getStackTraceAsString(e));
                allSuccess[0] = false;
            }
        });
        // 有一个不成功则置空IP 等下下次任务执行
        lastIp = allSuccess[0] ? ip : "";
        log.info("todoDdnsTask End");
    }
}
